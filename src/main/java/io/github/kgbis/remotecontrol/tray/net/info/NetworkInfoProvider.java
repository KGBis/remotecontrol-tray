package io.github.kgbis.remotecontrol.tray.net.info;

import com.google.inject.Inject;
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
		return networkChangeListener.getIpMacMap().getOrDefault(ip, "");
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
		log.debug("Waiting for network interfaces discovery");
		networkChangeListener.awaitInitialization(10000);
	}

	public List<String> getIPv4Addresses() {
		return new ArrayList<>(networkChangeListener.getIpMacMap().keySet());
	}

}
