package io.github.kgbis.remotecontrol.tray.net.info;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import oshi.hardware.NetworkIF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
@Slf4j
public class NetworkChangeListener {

	@Getter
	private final AtomicReference<Map<String, String>> atomicIpMacMap = new AtomicReference<>(Map.of());

	private final CountDownLatch initialized = new CountDownLatch(1);

	public void onNetworkChange(List<NetworkIF> activeInterfaces) {
		Map<String, String> newMap = new HashMap<>();
		activeInterfaces.forEach(net -> {
			for (String ip : net.getIPv4addr()) {
				newMap.put(ip, net.getMacaddr());
			}
		});

		atomicIpMacMap.set(Map.copyOf(newMap));
		atomicIpMacMap.get().forEach((ip, mac) -> log.info("Detected IP {} bound to MAC address {}", ip, mac));

		// Only for inialization
		if (!atomicIpMacMap.get().isEmpty()) {
			initialized.countDown();
		}
	}

	public void awaitInitialization(long timeoutMs) throws InterruptedException {
		log.info("Discovering network interfaces");
		if (!initialized.await(timeoutMs, TimeUnit.MILLISECONDS)) {
			log.debug("No network interfaces discovered within {} ms. Continuing anyway.", timeoutMs);
		}
		else {
			log.debug("Network interfaces discovered in less than {} ms.", timeoutMs);
		}
	}

}
