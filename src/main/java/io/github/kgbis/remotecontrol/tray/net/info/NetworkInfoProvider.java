/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.info;

import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class NetworkInfoProvider {

	@Getter
	private Map<String, String> addresses;

	public String getMac(String ip) {
		return addresses.getOrDefault(ip, "");
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
		return List.copyOf(addresses.keySet());
	}

	public void onChange(Map<InetAddress, String> data) {
		this.addresses = data.entrySet()
			.stream()
			.collect(Collectors.toMap(k -> k.getKey().getHostAddress(), Map.Entry::getValue));
	}

}
