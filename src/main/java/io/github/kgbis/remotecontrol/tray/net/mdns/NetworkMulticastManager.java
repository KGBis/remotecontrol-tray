/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.mdns;

import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.internal.NetworkInterfaces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.kgbis.remotecontrol.tray.net.server.NetworkServer.POLL_INTERVAL_MS;
import static io.github.kgbis.remotecontrol.tray.net.server.NetworkServer.PORT;

@Slf4j
@Singleton
public class NetworkMulticastManager {

	public static final String RPCCTL_TCP_LOCAL = "_rpcctl._tcp.local";

	public static final String RPCCT = "rpcct-";

	private final NetworkInterfaces networkInterfaces;

	private final JmDNSFactory jmDNSFactory;

	private final NetworkInfoProvider infoProvider;

	private Thread monitorThread;

	private volatile boolean running = false;

	private final Object lock = new Object();

	// Map of active Multicast per address
	Map<InetAddress, JmDNS> activeMdns = new ConcurrentHashMap<>();

	// Map of NetworkIF per address, just for the UI
	@Getter
	Map<InetAddress, String> addresses = new ConcurrentHashMap<>();

	@Inject
	public NetworkMulticastManager(NetworkInterfaces networkInterfaces, JmDNSFactory jmDNSFactory,
			NetworkInfoProvider infoProvider) {
		this.networkInterfaces = networkInterfaces;
		this.jmDNSFactory = jmDNSFactory;
		this.infoProvider = infoProvider;
	}

	public void start() {
		if (running) {
			return;
		}

		running = true;
		activeMdns.clear();

		monitorThread = new Thread(this::monitorLoop, "net-status-poller");
		monitorThread.setDaemon(true);
		monitorThread.start();
	}

	public void stop() {
		running = false;
		activeMdns.keySet().forEach(this::shutdownMdns);

		if (monitorThread != null)
			monitorThread.interrupt();
	}

	@SuppressWarnings("BusyWait")
	private void monitorLoop() {
		int pollIntervalMs = (int) (POLL_INTERVAL_MS * 7.5);

		while (running) {
			try {
				monitor();
				Thread.sleep(pollIntervalMs);
			}
			catch (InterruptedException | IOException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	void monitor() throws IOException {
		// get all valid addresses
		addresses.clear();
		addresses.putAll(networkInterfaces.getValidAddressesWithInterface());
		infoProvider.onChange(addresses);

		Set<InetAddress> current = addresses.keySet();
		Set<InetAddress> previous = activeMdns.keySet();

		// remove old
		for (InetAddress addr : CollectionUtils.subtract(previous, current)) {
			shutdownMdns(addr);
		}

		// add new
		for (InetAddress addr : CollectionUtils.subtract(current, previous)) {
			startMdns(addr);
		}
	}

	void startMdns(InetAddress inetAddress) throws IOException {
		synchronized (lock) {
			String hostAddress = inetAddress.getHostAddress();
			String serviceName = RPCCT + StringUtils.substringAfterLast(hostAddress, ".");

			Map<String, String> props = setProperties(inetAddress);
			ServiceInfo service = ServiceInfo.create(RPCCTL_TCP_LOCAL, serviceName, PORT, 0, 0, true, props);
			JmDNS jmdns = jmDNSFactory.create(inetAddress);
			jmdns.registerService(service);
			activeMdns.put(inetAddress, jmdns);
			log.info("mDNS service started at {}", hostAddress);
		}
	}

	void shutdownMdns(InetAddress inetAddress) {
		synchronized (lock) {
			String hostAddress = inetAddress.getHostAddress();

			JmDNS jmDNS = activeMdns.get(inetAddress);
			if (jmDNS != null) {
				jmDNS.unregisterAllServices();
				try {
					jmDNS.close();
					activeMdns.remove(inetAddress, jmDNS);
					log.info("mDNS service shutdown at {}", hostAddress);
				}
				catch (IOException e) {
					log.debug("Error while closing JmDNS: {}", e.getMessage());
				}
			}
		}
	}

	private Map<String, String> setProperties(InetAddress inetAddress) throws UnknownHostException {
		Map<String, String> props = new HashMap<>();
		props.put("version", ResourcesHelper.getVersion());
		props.put("os", System.getProperty("os.name"));
		props.put("hostname", InetAddress.getLocalHost().getHostName());
		props.put("mac", addresses.get(inetAddress));

		return props;
	}

}
