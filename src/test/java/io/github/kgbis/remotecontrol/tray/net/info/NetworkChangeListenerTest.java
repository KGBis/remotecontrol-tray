package io.github.kgbis.remotecontrol.tray.net.info;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import oshi.hardware.NetworkIF;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetworkChangeListenerTest {

	@Mock
	NetworkIF networkIF;

	@InjectMocks
	NetworkChangeListener listener;

	@Test
	void testOnNetworkChange_updatesIpMacMap() {
		when(networkIF.getIPv4addr()).thenReturn(new String[] { "192.168.0.1" });
		when(networkIF.getMacaddr()).thenReturn("AA:BB:CC:DD:EE:FF");

		listener.onNetworkChange(List.of(networkIF));

		assertEquals(1, listener.getAtomicIpMacMap().get().size());
		assertEquals("AA:BB:CC:DD:EE:FF", listener.getAtomicIpMacMap().get().get("192.168.0.1"));
	}

	@Test
	void testAwaitInitialization_unblocksWhenMapNotEmpty() throws InterruptedException {
		when(networkIF.getIPv4addr()).thenReturn(new String[] { "192.168.0.2" });
		when(networkIF.getMacaddr()).thenReturn("00:BB:00:DD:00:FF");

		listener.onNetworkChange(List.of(networkIF));

		long start = System.currentTimeMillis();
		listener.awaitInitialization(1000); // should unblock immediately
		long elapsed = System.currentTimeMillis() - start;

		assertTrue(elapsed < 200, "awaitInitialization took too long");
	}

	@Test
	void testAwaitInitialization_timeoutWithoutInterfaces() throws InterruptedException {
		long start = System.currentTimeMillis();
		listener.awaitInitialization(200); // no interfaces, should timeout
		long elapsed = System.currentTimeMillis() - start;

		assertTrue(elapsed >= 200, "awaitInitialization returned too early");
	}

}
