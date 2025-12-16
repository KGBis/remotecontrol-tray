package io.github.kgbis.remotecontrol.tray.net.mdns;

import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;
import static io.github.kgbis.remotecontrol.tray.net.server.NetworkServer.PORT;

@Slf4j
@Getter
@Singleton
public class ServiceRegistar {

	private JmDNS jmdns;

	private ServiceInfo service;

	private final AtomicReference<InetAddress> currentAddress =  new AtomicReference<>();

	private final Object lock = new Object();

	public void register() {
		if (jmdns == null) {
			try {
				currentAddress.set(selectMdnsAddress());
				service = ServiceInfo.create("_rpcctl._tcp.local.", REMOTE_PC_CONTROL, PORT,
						ResourcesHelper.getVersion());

				// @formatter:off
				/*
				Map<String,String> props = new HashMap<>();
				props.put("version", ResourcesHelper.getVersion());
				props.put("os", System.getProperty("os.name"));
				props.put("id", "PC-OFICINA");
				props.put("auth", "token123"); // opcional para validación

				service = ServiceInfo.create("_rpcctl._tcp.local.",
                             REMOTE_PC_CONTROL,
                             PORT,
                             0, // prioridad (opcional)
                             0, // peso (opcional)
                             true, // habilitar txt record
                             props);
				 */
				// @formatter:on

				// create and register mDNS
				jmdns = JmDNS.create(currentAddress.get());
				jmdns.registerService(service);

				log.info("mDNS service registered as {}.", service);
			}
			catch (IOException e) {
				log.warn("Service could not be registered: {}", e.getMessage());
			}
		}

	}

	public void restartIfNeeded() {
		synchronized (lock) {
			try {
				InetAddress newAddr = selectMdnsAddress();

				if (newAddr.equals(currentAddress.get())) {
					return; // nothing changed
				}

				log.info("Network change detected, restarting mDNS: {} -> {}", currentAddress, newAddr);

				unregisterInternal();
				currentAddress.set(newAddr);
				jmdns = JmDNS.create(newAddr);
				jmdns.registerService(service);

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
				log.info("mDNS service unregistered and closed.");
			}
			catch (IOException e) {
				log.debug("Error while closing JmDNS: {}", e.getMessage());
			}
			jmdns = null;
		}
	}

	private InetAddress selectMdnsAddress() throws SocketException {
		for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
			if (!ni.isUp() || ni.isLoopback() || ni.isVirtual() || !ni.supportsMulticast())
				continue;

			for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
				if (addr instanceof Inet4Address) {
					return addr;
				}
			}
		}
		throw new IllegalStateException("No suitable network interface for mDNS");
	}

}
