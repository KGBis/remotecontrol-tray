package io.github.kgbis.remotecontrol.tray.net.internal;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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

@Slf4j
@Singleton
public class NetworkInterfaces {

	private final SystemInfo systemInfo;

	final String[] virtualPlaceholders = new String[] { "virtual", "docker", "br-", "virbr", "vbox",
			"hyper" };

	@Inject
    public NetworkInterfaces(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    public List<NetworkIF> getActiveInterfaces() {
		List<NetworkIF> result = new ArrayList<>();
		for (NetworkIF net : systemInfo.getHardware().getNetworkIFs()) {
			boolean hasIPv4 = Arrays.stream(net.getIPv4addr()).anyMatch(ip -> !ip.isEmpty());
			if (hasIPv4)
				result.add(net);
		}
		return result;
	}

	public Optional<InetAddress> selectMdnsAddress() {
		return getActiveInterfaces().stream()
			.filter(this::isUsable)
			.filter(net -> !isVirtualLike(net))
			.sorted(Comparator.comparingInt(this::priority))
			.flatMap(net -> Arrays.stream(net.getIPv4addr()))
			.map(this::toInetAddress)
			.flatMap(Optional::stream)
			.findFirst();
	}

	boolean isVirtualLike(NetworkIF networkIF) {
		return Strings.CI.containsAny(networkIF.getName(), virtualPlaceholders);
	}

	int priority(NetworkIF networkIF) {
		String name = networkIF.getName().toLowerCase();
		if (name.startsWith("eth") || name.startsWith("en")) { // "en" for macOS
			return 0;
		}

		if (name.startsWith("wlan") || name.startsWith("wl")) {
			return 1;
		}

		return 10;
	}

	boolean isLoopback(NetworkIF networkIF) {
		try {
			NetworkInterface ni = networkIF.queryNetworkInterface();
			return ni != null && ni.isLoopback();
		}
		catch (SocketException e) {
			return true; // if error, discard
		}
	}

	boolean isUsable(NetworkIF networkIF) {
		if (!networkIF.isConnectorPresent()) {
			return false;
		}
		if (isLoopback(networkIF)) {
			return false;
		}

		return ArrayUtils.isNotEmpty(networkIF.getIPv4addr());
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
