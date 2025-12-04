package io.github.kgbis.remotecontrol.tray.net.info;

import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkChangeRegistrar {

	private final SystemInfo systemInfo = new SystemInfo();

	private final List<NetworkChangeListener> listeners = new CopyOnWriteArrayList<>();

	private Map<String, String> lastIpMacMap = new HashMap<>();

	private final int pollIntervalMs;

	private Thread monitorThread;

	private volatile boolean running = false;

	public NetworkChangeRegistrar(int pollIntervalMs) {
		this.pollIntervalMs = pollIntervalMs;
	}

	public void addListener(NetworkChangeListener callback) {
		listeners.add(callback);
	}

	public void removeListener(NetworkChangeListener callback) {
		listeners.remove(callback);
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
					lastIpMacMap = currentIpMacMap;
					for (NetworkChangeListener listener : listeners) {
						listener.onNetworkChange(activeInterfaces);
					}
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
