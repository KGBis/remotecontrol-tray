package com.kikegg.remote.pc.control.network.server;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NetworkInfoProvider {

	private final NetworkChangeCallbackImpl callback;

	public NetworkInfoProvider(NetworkChangeCallbackImpl callback) {
		this.callback = callback;
		registerNetworkCallback(callback);
	}

	public String getMac(String ip) {
		return callback.getIpMacMap().getOrDefault(ip, "");
	}

	public String getHostName(String ip) {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e) {
			return ip;
		}
	}

	public List<String> getIPv4Addresses() {
		return new ArrayList<>(callback.getIpMacMap().keySet());
	}

	private void registerNetworkCallback(NetworkChangeCallbackImpl networkChangeCallback) {
		NetworkChangeListener listener = new NetworkChangeListener(1000);
		listener.addListener(networkChangeCallback);

		log.info("Starting NetworkChangeListener");
		listener.start();

		// register shutdown hook to remove listener daemon
        log.info("Registering shutdown hook");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			listener.removeListener(networkChangeCallback);
			listener.stop();
		}));
	}

}
