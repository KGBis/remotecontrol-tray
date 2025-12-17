package io.github.kgbis.remotecontrol.tray.net.info;

import io.github.kgbis.remotecontrol.tray.net.internal.NetworkInterfaces;
import io.github.kgbis.remotecontrol.tray.net.mdns.MulticastServiceRegistar;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import oshi.hardware.NetworkIF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.github.kgbis.remotecontrol.tray.net.server.NetworkServer.POLL_INTERVAL_MS;

@Singleton
public class NetworkChangeRegistrar {

	private final MulticastServiceRegistar multicastServiceRegistar;

	private final NetworkInterfaces interfaces;

	private final List<NetworkChangeListener> listeners = new CopyOnWriteArrayList<>();

	private Map<String, String> lastIpMacMap = new HashMap<>();

	@Setter
	private int pollIntervalMs = POLL_INTERVAL_MS;

	private Thread monitorThread;

	private volatile boolean running = false;

	@Inject
	public NetworkChangeRegistrar(MulticastServiceRegistar multicastServiceRegistar, NetworkInterfaces interfaces) {
		this.multicastServiceRegistar = multicastServiceRegistar;
        this.interfaces = interfaces;
    }

	public void addListener(NetworkChangeListener listener) {
		listeners.add(listener);
	}

	public void removeListener(NetworkChangeListener listener) {
		listeners.remove(listener);
	}

	public void start() {
		if (running)
			return;
		running = true;
		monitorThread = new Thread(this::monitorLoop, "net-info-poller");
		monitorThread.setDaemon(true);
		monitorThread.start();
	}

	public void stop() {
		running = false;
		if (monitorThread != null)
			monitorThread.interrupt();
	}

	@SuppressWarnings("BusyWait")
	private void monitorLoop() {
		while (running) {
			try {
				List<NetworkIF> activeInterfaces = interfaces.getActiveInterfaces();
				Map<String, String> currentIpMacMap = getCurrentIpMacMap(activeInterfaces);

				if (!currentIpMacMap.equals(lastIpMacMap)) {
					// changes occurred
					lastIpMacMap = currentIpMacMap;
					for (NetworkChangeListener listener : listeners) {
						listener.onNetworkChange(activeInterfaces);
					}
					if (!lastIpMacMap.isEmpty())
						multicastServiceRegistar.restartIfNeeded();

				}

				Thread.sleep(pollIntervalMs);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private @NonNull Map<String, String> getCurrentIpMacMap(List<NetworkIF> activeInterfaces) {
		Map<String, String> currentIpMacMap = new HashMap<>();
		for (NetworkIF net : activeInterfaces) {
			for (String ip : net.getIPv4addr()) {
				currentIpMacMap.put(ip, net.getMacaddr());
			}
		}
		return currentIpMacMap;
	}

}
