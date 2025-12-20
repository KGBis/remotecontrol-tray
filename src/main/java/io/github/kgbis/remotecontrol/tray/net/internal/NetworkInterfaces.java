/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.internal;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.tuple.Pair;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Singleton
public class NetworkInterfaces {

	private final SystemInfo systemInfo;

	final String[] virtualPlaceholders = new String[] { "virtual", "docker", "br-", "virbr", "vbox", "hyper" };

	@Inject
	public NetworkInterfaces(SystemInfo systemInfo) {
		this.systemInfo = systemInfo;
	}

	public Map<InetAddress, String> getValidAddressesWithInterface() {
		return getActiveInterfaces().stream()
			.filter(this::isUsable)
			.filter(this::hasIpv4Gateway)
			.filter(net -> !isVirtualLike(net) && !isLoopback(net))
			.flatMap(networkIF -> Arrays.stream(networkIF.getIPv4addr())
				.map(this::toInetAddress)
				.filter(Optional::isPresent)
				.flatMap(inetAddress -> Stream.of(Pair.of(inetAddress.get(), networkIF.getMacaddr()))))
			.collect(Collectors.toMap(Pair::getLeft, Pair::getRight, (a, b) -> a));
	}

	List<NetworkIF> getActiveInterfaces() {
		List<NetworkIF> result = new ArrayList<>();
		for (NetworkIF net : systemInfo.getHardware().getNetworkIFs()) {
			boolean hasIPv4 = Arrays.stream(net.getIPv4addr()).anyMatch(ip -> !ip.isEmpty());
			if (hasIPv4)
				result.add(net);
		}
		return result;
	}

	boolean hasIpv4Gateway(NetworkIF net) {
		try {
			NetworkInterface ni = net.queryNetworkInterface();
			if (ni == null)
				return false;

			return ni.getInterfaceAddresses()
				.stream()
				.anyMatch(addr -> addr.getAddress() instanceof Inet4Address && addr.getBroadcast() != null);
		}
		catch (Exception e) {
			return false;
		}
	}

	boolean isVirtualLike(NetworkIF networkIF) {
		return Strings.CI.containsAny(networkIF.getName(), virtualPlaceholders);
	}

	boolean isLoopback(NetworkIF net) {
		try {
			NetworkInterface ni = net.queryNetworkInterface();
			return ni != null && ni.isLoopback();
		}
		catch (SocketException e) {
			return true; // if error, discard
		}
	}

	boolean isUsable(NetworkIF net) {
		return net.getIfOperStatus() == NetworkIF.IfOperStatus.UP && net.isConnectorPresent()
				&& net.getIPv4addr().length > 0;
	}

	Optional<InetAddress> toInetAddress(String ip) {
		try {
			return Optional.of(InetAddress.getByName(ip));
		}
		catch (Exception e) {
			return Optional.empty();
		}
	}

}
