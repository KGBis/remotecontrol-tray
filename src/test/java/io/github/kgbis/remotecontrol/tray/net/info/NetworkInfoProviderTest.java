package io.github.kgbis.remotecontrol.tray.net.info;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import oshi.hardware.NetworkIF;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetworkInfoProviderTest {

	@Mock
	NetworkIF networkIF;

	@Test
	void testGetMacAndIPv4Addresses() {
		when(networkIF.getIPv4addr()).thenReturn(new String[] { "10.0.0.1" });
		when(networkIF.getMacaddr()).thenReturn("AA:AA:AA:AA:AA:AA");

		NetworkChangeListener listener = new NetworkChangeListener();
		listener.onNetworkChange(List.of(networkIF));

		NetworkInfoProvider provider = new NetworkInfoProvider(listener);

		assertEquals("AA:AA:AA:AA:AA:AA", provider.getMac("10.0.0.1"));
		assertEquals(List.of("10.0.0.1"), provider.getIPv4Addresses());
	}

}
