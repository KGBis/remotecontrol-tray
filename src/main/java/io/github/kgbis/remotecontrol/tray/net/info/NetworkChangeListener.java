package io.github.kgbis.remotecontrol.tray.net.info;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import oshi.hardware.NetworkIF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NetworkChangeListener {

	@Getter
	private final Map<String, String> ipMacMap = new ConcurrentHashMap<>();

	private final CountDownLatch initialized = new CountDownLatch(1);

	public void onNetworkChange(List<NetworkIF> activeInterfaces) {
		Map<String, String> newMap = new HashMap<>();
		activeInterfaces.forEach(net -> {
			for (String ip : net.getIPv4addr()) {
				newMap.put(ip, net.getMacaddr());
			}
		});
		ipMacMap.clear();
		ipMacMap.putAll(newMap);

		ipMacMap.forEach((ip, mac) -> log.info("Detected IP {} bound to MAC addres {}", ip, mac));

		if (!ipMacMap.isEmpty()) {
			initialized.countDown();
		}
	}

	public void awaitInitialization(long timeoutMs) throws InterruptedException {
		if (!initialized.await(timeoutMs, TimeUnit.MILLISECONDS)) {
			log.warn("No network interfaces discovered within {} ms. Continuing anyway.", timeoutMs);
		}
		else {
			log.debug("Network interfaces discovered");
		}
	}

}
