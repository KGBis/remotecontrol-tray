package io.github.kgbis.remotecontrol.tray.net.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.Optional;

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
	void testSelectMdnsAddress() throws Exception {
		when(systemInfo.getHardware()).thenReturn(hardwareAbstractionLayer);
		when(hardwareAbstractionLayer.getNetworkIFs()).thenReturn(List.of(networkIF));
		when(networkIF.getIPv4addr()).thenReturn(new String[] { "192.168.1.100" });
		when(networkIF.isConnectorPresent()).thenReturn(true);
		when(networkIF.getName()).thenReturn("eth0");
		when(networkIF.queryNetworkInterface()).thenReturn(NetworkInterface.getByName("eth0"));

		Optional<InetAddress> address = networkInterfaces.selectMdnsAddress();
		assertTrue(address.isPresent());
		assertEquals("192.168.1.100", address.get().getHostAddress());
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void testSelectMdnsAddress_mixedInterfaces(boolean connPresent) throws Exception {
		NetworkIF ethMock = mock(NetworkIF.class);

		// to avoid stubbing errors
		if (connPresent) {
			when(ethMock.getName()).thenReturn("eth0");
			when(ethMock.queryNetworkInterface()).thenReturn(NetworkInterface.getByName("eth0"));
		}
		when(ethMock.getIPv4addr()).thenReturn(new String[] { "192.168.1.10" });
		when(ethMock.isConnectorPresent()).thenReturn(connPresent);

		NetworkIF wlanMock = mock(NetworkIF.class);
		when(wlanMock.getName()).thenReturn("wlan0");
		when(wlanMock.getIPv4addr()).thenReturn(new String[] { "192.168.1.20" });
		when(wlanMock.isConnectorPresent()).thenReturn(true);
		when(wlanMock.queryNetworkInterface()).thenReturn(NetworkInterface.getByName("wlan0"));

		NetworkIF dockerMock = mock(NetworkIF.class);
		when(dockerMock.getName()).thenReturn("docker0");
		when(dockerMock.getIPv4addr()).thenReturn(new String[] { "172.17.0.1" });
		when(dockerMock.isConnectorPresent()).thenReturn(true);
		when(dockerMock.queryNetworkInterface()).thenReturn(NetworkInterface.getByName("docker0"));

		when(systemInfo.getHardware()).thenReturn(hardwareAbstractionLayer);
		when(hardwareAbstractionLayer.getNetworkIFs()).thenReturn(List.of(ethMock, wlanMock, dockerMock));

		Optional<InetAddress> address = networkInterfaces.selectMdnsAddress();
		assertTrue(address.isPresent());

		if (connPresent)
			assertEquals("192.168.1.10", address.get().getHostAddress());
		else
			assertEquals("192.168.1.20", address.get().getHostAddress());
	}

	@Test
	void testSelectMdnsAddress_noneAvailable() throws Exception {
		when(systemInfo.getHardware()).thenReturn(hardwareAbstractionLayer);
		when(hardwareAbstractionLayer.getNetworkIFs()).thenReturn(List.of(networkIF));
		when(networkIF.getIPv4addr()).thenReturn(new String[] { "127.0.0.1" });
		when(networkIF.isConnectorPresent()).thenReturn(true);
		when(networkIF.queryNetworkInterface()).thenReturn(NetworkInterface.getByName("lo"));

		Optional<InetAddress> address = networkInterfaces.selectMdnsAddress();
		assertTrue(address.isEmpty());
	}

	@Test
	void testIsVirtualLike() {
		NetworkIF mockNet = mock(NetworkIF.class);
		when(mockNet.getName()).thenReturn("vbox0");
		assertTrue(networkInterfaces.isVirtualLike(mockNet));
	}

	@Test
	void testPriority() {
		NetworkIF eth = mock(NetworkIF.class);
		when(eth.getName()).thenReturn("eth0");
		assertEquals(0, networkInterfaces.priority(eth));
	}

}