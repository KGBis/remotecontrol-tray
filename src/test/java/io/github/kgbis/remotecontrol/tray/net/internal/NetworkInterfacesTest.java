/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetworkInterfacesTest {

	@Mock
	SystemInfo systemInfo;

	@Mock
	HardwareAbstractionLayer hardwareAbstractionLayer;

	@Mock
	NetworkIF networkIF;

	@InjectMocks
	NetworkInterfaces networkInterfaces;

	@Test
	void getActiveInterfaces_noInterfaces() {
		when(systemInfo.getHardware()).thenReturn(hardwareAbstractionLayer);
		when(hardwareAbstractionLayer.getNetworkIFs()).thenReturn(List.of());
		List<NetworkIF> activeInterfaces = networkInterfaces.getActiveInterfaces();
		assertEquals(0, activeInterfaces.size());
	}

	@Test
	void getActiveInterfaces_interfaceList() {
		when(systemInfo.getHardware()).thenReturn(hardwareAbstractionLayer);
		when(hardwareAbstractionLayer.getNetworkIFs()).thenReturn(Collections.singletonList(networkIF));
		when(networkIF.getIPv4addr()).thenReturn(new String[] { "192.168.1.144" });
		List<NetworkIF> activeInterfaces = networkInterfaces.getActiveInterfaces();
		assertEquals(1, activeInterfaces.size());
	}

	@Test
	void getValidAddressesWithInterface_noneFound() {
		when(systemInfo.getHardware()).thenReturn(hardwareAbstractionLayer);
		when(hardwareAbstractionLayer.getNetworkIFs()).thenReturn(List.of());

		Map<InetAddress, String> validAddressesWithInterface = networkInterfaces.getValidAddressesWithInterface();

		assertEquals(0, validAddressesWithInterface.size());
	}

	@Test
	void getValidAddressesWithInterface_foundValidInterfaces() throws UnknownHostException {
		InetAddress inetAddress1 = InetAddress.getByName("192.168.1.144");
		InetAddress inetAddress2 = InetAddress.getByName("192.168.1.43");

		NetworkInterface networkInterface1 = mock(NetworkInterface.class);
		InterfaceAddress interfaceAddress1 = mock(InterfaceAddress.class);

		when(networkInterface1.getInterfaceAddresses()).thenReturn(Collections.singletonList(interfaceAddress1));
		when(interfaceAddress1.getAddress()).thenReturn(inetAddress1);
		when(interfaceAddress1.getBroadcast()).thenReturn(inetAddress1);

		InterfaceAddress interfaceAddress2 = mock(InterfaceAddress.class);
		NetworkInterface networkInterface2 = mock(NetworkInterface.class);

		when(networkInterface2.getInterfaceAddresses()).thenReturn(Collections.singletonList(interfaceAddress2));
		when(interfaceAddress2.getAddress()).thenReturn(inetAddress2);
		when(interfaceAddress2.getBroadcast()).thenReturn(inetAddress2);

		NetworkIF networkIF2 = mock(NetworkIF.class);
		when(systemInfo.getHardware()).thenReturn(hardwareAbstractionLayer);
		when(hardwareAbstractionLayer.getNetworkIFs()).thenReturn(List.of(networkIF, networkIF2));

		when(networkIF.getIfOperStatus()).thenReturn(NetworkIF.IfOperStatus.UP);
		when(networkIF.isConnectorPresent()).thenReturn(true);
		when(networkIF.getIPv4addr()).thenReturn(new String[] { "192.168.1.144" });
		when(networkIF.queryNetworkInterface()).thenReturn(networkInterface1);
		when(networkIF.getMacaddr()).thenReturn("00:11:22:33:44:55");
		when(networkIF.getName()).thenReturn("eth2");

		when(networkIF2.getIfOperStatus()).thenReturn(NetworkIF.IfOperStatus.UP);
		when(networkIF2.isConnectorPresent()).thenReturn(true);
		when(networkIF2.getIPv4addr()).thenReturn(new String[] { "192.168.1.43" });
		when(networkIF2.queryNetworkInterface()).thenReturn(networkInterface2);
		when(networkIF2.getMacaddr()).thenReturn("00:00:22:33:44:0A");
		when(networkIF2.getName()).thenReturn("wlan0");

		Map<InetAddress, String> validAddressesWithInterface = networkInterfaces.getValidAddressesWithInterface();

		assertEquals(2, validAddressesWithInterface.size());
	}

	@Test
	void testIsVirtualLike() {
		NetworkIF mockNet = mock(NetworkIF.class);
		when(mockNet.getName()).thenReturn("vbox0");
		assertTrue(networkInterfaces.isVirtualLike(mockNet));
	}

	@Test
	void invalidIp_isDiscarded() {
		when(systemInfo.getHardware()).thenReturn(hardwareAbstractionLayer);
		when(hardwareAbstractionLayer.getNetworkIFs()).thenReturn(List.of(networkIF));

		when(networkIF.getIfOperStatus()).thenReturn(NetworkIF.IfOperStatus.UP);
		when(networkIF.isConnectorPresent()).thenReturn(true);
		when(networkIF.getIPv4addr()).thenReturn(new String[] { "not-an-ip" });

		Map<InetAddress, String> result = networkInterfaces.getValidAddressesWithInterface();

		assertTrue(result.isEmpty());
	}

	@Test
	void loopbackInterface_isDiscarded() {
		NetworkInterface ni = mock(NetworkInterface.class);

		when(systemInfo.getHardware()).thenReturn(hardwareAbstractionLayer);
		when(hardwareAbstractionLayer.getNetworkIFs()).thenReturn(List.of(networkIF));

		when(networkIF.getIfOperStatus()).thenReturn(NetworkIF.IfOperStatus.UP);
		when(networkIF.isConnectorPresent()).thenReturn(true);
		when(networkIF.getIPv4addr()).thenReturn(new String[] { "127.0.0.1" });
		when(networkIF.queryNetworkInterface()).thenReturn(ni);

		Map<InetAddress, String> result = networkInterfaces.getValidAddressesWithInterface();

		assertTrue(result.isEmpty());
	}

}