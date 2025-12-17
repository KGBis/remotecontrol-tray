package io.github.kgbis.remotecontrol.tray.net.mdns;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.internal.NetworkInterfaces;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MulticastServiceRegistarTest {

	@Mock
	NetworkInterfaces interfaces;

	@Mock
	NetworkInfoProvider networkInfoProvider;

	@Mock
	JmDNS jmDNS;

	@Mock
	JmDNSFactory jmDNSFactory;

	@Mock
	InetAddress address;

	@Spy
	@InjectMocks
	MulticastServiceRegistar multicastServiceRegistar;

	@Test
	void register() throws IOException {
		when(interfaces.selectMdnsAddress()).thenReturn(Optional.of(address));
		when(address.getHostAddress()).thenReturn("192.168.1.21");
		when(networkInfoProvider.getHostName(anyString())).thenReturn("test-host");
		when(networkInfoProvider.getMac(anyString())).thenReturn("AA:BB:CC:DD:EE:FF");
		when(jmDNSFactory.create(any(InetAddress.class))).thenReturn(jmDNS);

		multicastServiceRegistar.register();
		verify(jmDNS, times(1)).registerService(any());
	}

	@Test
	void register_noNetworkInterfacesDetected() throws IOException {
		when(interfaces.selectMdnsAddress()).thenReturn(Optional.empty());

		multicastServiceRegistar.register();
		verify(jmDNS, times(0)).registerService(any());
	}

	@Test
	void restartIfNeeded() throws IOException {
		InetAddress addr1 = InetAddress.getByName("192.168.1.10");
		InetAddress addr2 = InetAddress.getByName("192.168.1.20");

		when(interfaces.selectMdnsAddress()).thenReturn(Optional.of(addr1))
			.thenReturn(Optional.of(addr1))
			.thenReturn(Optional.of(addr2));
		doNothing().when(multicastServiceRegistar).register();

		multicastServiceRegistar.restartIfNeeded();
		assertEquals(multicastServiceRegistar.getCurrentAddress().get(), addr1);
		verify(multicastServiceRegistar, times(1)).register();

		// Refresh called without changes
		multicastServiceRegistar.restartIfNeeded();
		assertEquals(multicastServiceRegistar.getCurrentAddress().get(), addr1);
		verify(multicastServiceRegistar, times(1)).register();

		// Refresh called with changes
		multicastServiceRegistar.restartIfNeeded();
		assertEquals(multicastServiceRegistar.getCurrentAddress().get(), addr2);
		verify(multicastServiceRegistar, times(2)).register(); // Cumulative!
	}

	@Test
	void unregister_doesNothingWhenNotRegistered() {
		multicastServiceRegistar.unregister();
		verify(jmDNS, times(0)).unregisterService(any());
	}

	@Test
	void unregister() throws IOException {
		when(jmDNSFactory.create(any())).thenReturn(jmDNS);
		when(interfaces.selectMdnsAddress()).thenReturn(Optional.of(address));

		// act
		multicastServiceRegistar.register();
		multicastServiceRegistar.unregister();

		// assert
		verify(jmDNS).unregisterAllServices();
		verify(jmDNS).close();
	}

}