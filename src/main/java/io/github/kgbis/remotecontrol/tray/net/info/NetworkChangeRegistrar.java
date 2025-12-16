package io.github.kgbis.remotecontrol.tray.net.info;

import io.github.kgbis.remotecontrol.tray.net.mdns.ServiceRegistar;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Setter;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.github.kgbis.remotecontrol.tray.net.server.NetworkServer.POLL_INTERVAL_MS;

@Singleton
public class NetworkChangeRegistrar {

	private final ServiceRegistar serviceRegistar;

	private final SystemInfo systemInfo = new SystemInfo();

	private final List<NetworkChangeListener> listeners = new CopyOnWriteArrayList<>();

	private Map<String, String> lastIpMacMap = new HashMap<>();

	@Setter
	private int pollIntervalMs = POLL_INTERVAL_MS;

	private Thread monitorThread;

	private volatile boolean running = false;

	@Inject
    public NetworkChangeRegistrar(ServiceRegistar serviceRegistar) {
        this.serviceRegistar = serviceRegistar;
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
				List<NetworkIF> activeInterfaces = getActiveInterfaces();
				Map<String, String> currentIpMacMap = new HashMap<>();
				for (NetworkIF net : activeInterfaces) {
					for (String ip : net.getIPv4addr()) {
						currentIpMacMap.put(ip, net.getMacaddr());
					}
				}

				if (!currentIpMacMap.equals(lastIpMacMap)) {
					// changes occurred
					lastIpMacMap = currentIpMacMap;
					for (NetworkChangeListener listener : listeners) {
						listener.onNetworkChange(activeInterfaces);
					}
					serviceRegistar.restartIfNeeded();

				}

				Thread.sleep(pollIntervalMs);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private List<NetworkIF> getActiveInterfaces() {
		List<NetworkIF> result = new ArrayList<>();
		for (NetworkIF net : systemInfo.getHardware().getNetworkIFs()) {
			boolean hasIPv4 = Arrays.stream(net.getIPv4addr()).anyMatch(ip -> !ip.isEmpty());
			if (hasIPv4)
				result.add(net);
		}
		return result;
	}

}
