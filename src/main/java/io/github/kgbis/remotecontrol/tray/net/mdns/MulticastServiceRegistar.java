package io.github.kgbis.remotecontrol.tray.net.mdns;

import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.kgbis.remotecontrol.tray.net.NetworkInterfacesHelper.selectMdnsAddress;
import static io.github.kgbis.remotecontrol.tray.net.server.NetworkServer.PORT;

@Slf4j
@Getter
@Singleton
public class MulticastServiceRegistar {

	private JmDNS jmdns;

	private ServiceInfo service;

	private final AtomicReference<InetAddress> currentAddress = new AtomicReference<>();

	private final Object lock = new Object();

	private final NetworkInfoProvider networkInfoProvider;

	@Inject
	public MulticastServiceRegistar(NetworkInfoProvider networkInfoProvider) {
		this.networkInfoProvider = networkInfoProvider;
	}

	public void register() {
		synchronized (lock) {
			if (jmdns == null) {
				try {
					InetAddress inetAddress = selectMdnsAddress().orElse(null);
					if (inetAddress == null) {
						log.error("Unable to find a suitable mDNS address. No mDNS will be registered.");
						return;
					}
					currentAddress.set(inetAddress);
					Map<String, String> props = setProperties(inetAddress.getHostAddress());
					service = ServiceInfo.create("_rpcctl._tcp.local", "rpcct", PORT, 0, 0, true, props);

					// create and register mDNS
					jmdns = JmDNS.create(currentAddress.get());
					jmdns.registerService(service);

					log.info("mDNS service registered as {}.", service.getNiceTextString());
				}
				catch (IOException e) {
					log.warn("Service could not be registered: {}", e.getMessage());
				}
			}
		}

	}

	public void restartIfNeeded() {
		synchronized (lock) {
			try {
				InetAddress newAddr = selectMdnsAddress().orElse(null);

				// in case of no new address, unregister and exit
				if (newAddr == null) {
					unregisterInternal();
					return;
				}

				if (newAddr.equals(currentAddress.get())) {
					return; // nothing changed
				}

				// only log if network actually changed
				if (currentAddress.get() == null) {
					log.info("Network change detected from {} to {}, restarting mDNS", currentAddress, newAddr);
				}

				unregisterInternal();
				currentAddress.set(newAddr);
				register();

			}
			catch (Exception e) {
				log.warn("Failed to restart mDNS: {}", e.getMessage());
			}
		}
	}

	public void unregister() {
		unregisterInternal();
	}

	private void unregisterInternal() {
		if (jmdns != null) {
			jmdns.unregisterAllServices();
			try {
				jmdns.close();
				log.debug("mDNS service unregistered and closed.");
			}
			catch (IOException e) {
				log.debug("Error while closing JmDNS: {}", e.getMessage());
			}
			jmdns = null;
		}
	}

	private Map<String, String> setProperties(String hostAddress) {
		Map<String, String> props = new HashMap<>();
		props.put("version", ResourcesHelper.getVersion());
		props.put("os", System.getProperty("os.name"));
		props.put("hostname", networkInfoProvider.getHostName(hostAddress));
		props.put("mac", networkInfoProvider.getMac(hostAddress));

		return props;
	}

}
