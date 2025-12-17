package io.github.kgbis.remotecontrol.tray.net.info;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@Singleton
@Slf4j
public class NetworkInfoProvider {

	@Getter
	private final NetworkChangeListener networkChangeListener;

	@Inject
	public NetworkInfoProvider(NetworkChangeListener networkChangeListener) {
		this.networkChangeListener = networkChangeListener;
	}

	public String getMac(String ip) {
		return networkChangeListener.getAtomicIpMacMap().get().getOrDefault(ip, "");
	}

	public String getHostName(String ip) {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e) {
			return ip;
		}
	}

	public void awaitInitialization() throws InterruptedException {
		networkChangeListener.awaitInitialization(7500);
	}

	public List<String> getIPv4Addresses() {
		return new ArrayList<>(networkChangeListener.getAtomicIpMacMap().get().keySet());
	}

}
