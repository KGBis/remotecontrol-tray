package io.github.kgbis.remotecontrol.tray.net.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class NetworkInfoProvider {

	@Getter
	private final NetworkChangeCallbackImpl callback;

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

}
