package io.github.kgbis.remotecontrol.tray.net;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Strings;
import oshi.SystemInfo;
import oshi.hardware.NetworkIF;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class NetworkInterfacesHelper {

	private static final SystemInfo systemInfo = new SystemInfo();

	private static final String[] virtualPlaceholders = new String[] { "virtual", "docker", "br-", "virbr", "vbox",
			"hyper" };

	public static List<NetworkIF> getActiveInterfaces() {
		List<NetworkIF> result = new ArrayList<>();
		for (NetworkIF net : systemInfo.getHardware().getNetworkIFs()) {
			boolean hasIPv4 = Arrays.stream(net.getIPv4addr()).anyMatch(ip -> !ip.isEmpty());
			if (hasIPv4)
				result.add(net);
		}
		return result;
	}

	public static Optional<InetAddress> selectMdnsAddress() {
		return getActiveInterfaces().stream()
			.filter(NetworkInterfacesHelper::isUsable)
			.filter(net -> !isVirtualLike(net))
			.sorted(Comparator.comparingInt(NetworkInterfacesHelper::priority))
			.flatMap(net -> Arrays.stream(net.getIPv4addr()))
			.map(NetworkInterfacesHelper::toInetAddress)
			.flatMap(Optional::stream)
			.findFirst();
	}

	private static boolean isVirtualLike(NetworkIF networkIF) {
		return Strings.CI.containsAny(networkIF.getName(), virtualPlaceholders);
	}

	private static int priority(NetworkIF networkIF) {
		String name = networkIF.getName().toLowerCase();
		if (name.startsWith("eth") || name.startsWith("en")) { // "en" for macOS
			return 0;
		}

		if (name.startsWith("wlan") || name.startsWith("wl")) {
			return 1;
		}

		return 10;
	}

	private static boolean isLoopback(NetworkIF networkIF) {
		try {
			NetworkInterface ni = networkIF.queryNetworkInterface();
			return ni != null && ni.isLoopback();
		}
		catch (SocketException e) {
			return true; // if error, discard
		}
	}

	private static boolean isUsable(NetworkIF networkIF) {
		if (!networkIF.isConnectorPresent()) {
			return false;
		}
		if (isLoopback(networkIF)) {
			return false;
		}

		return ArrayUtils.isNotEmpty(networkIF.getIPv4addr());
	}

	private static Optional<InetAddress> toInetAddress(String ip) {
		try {
			return Optional.of(InetAddress.getByName(ip));
		}
		catch (Exception e) {
			return Optional.empty();
		}
	}

}
