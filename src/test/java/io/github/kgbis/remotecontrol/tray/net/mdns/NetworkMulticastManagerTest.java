/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
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
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetworkMulticastManagerTest {

	@Mock
	NetworkInterfaces interfaces;

	@Mock
	JmDNS jmDNS;

	@Mock
	JmDNSFactory jmDNSFactory;

	@Mock
	NetworkInfoProvider infoProvider;

	@Spy
	@InjectMocks
	NetworkMulticastManager networkMulticastManager;

	@Test
	void start_callsRegister() throws IOException {
		InetAddress address = InetAddress.getByName("192.168.1.144");

		when(jmDNSFactory.create(any(InetAddress.class))).thenReturn(jmDNS);

		networkMulticastManager.startMdns(address);

		verify(jmDNS, times(1)).registerService(any());
	}

	@Test
	void stop_callsShutdownMdns() throws IOException {
		InetAddress address = InetAddress.getByName("192.168.1.144");
		when(jmDNSFactory.create(any(InetAddress.class))).thenReturn(jmDNS);

		networkMulticastManager.startMdns(address);
		networkMulticastManager.stop();
		verify(networkMulticastManager, times(1)).shutdownMdns(address);
	}

	@Test
	void stop_doesNothingWhenNotRegistered() {
		networkMulticastManager.stop();
		verify(networkMulticastManager, times(0)).shutdownMdns(any());
	}

	@Test
	void shutdown_CallsShutdownMdns() throws IOException {
		InetAddress address = InetAddress.getByName("192.168.1.144");
		when(jmDNSFactory.create(any(InetAddress.class))).thenReturn(jmDNS);

		networkMulticastManager.startMdns(address);
		networkMulticastManager.shutdownMdns(address);
		verify(jmDNS, times(1)).unregisterAllServices();
	}

	@Test
	void shutdown_doesNothingWhenNotRegistered() throws UnknownHostException {
		InetAddress address = InetAddress.getByName("192.168.1.144");
		networkMulticastManager.shutdownMdns(address);
		verify(jmDNS, times(0)).unregisterAllServices();
	}

	@Test
	void monitor_registersNewAddress() throws Exception {
		InetAddress address = InetAddress.getByName("192.168.1.144");
		Map<InetAddress, String> map = Map.of(address, "00:11:22:33:44:55");

		when(interfaces.getValidAddressesWithInterface()).thenReturn(map);
		when(jmDNSFactory.create(address)).thenReturn(jmDNS);

		networkMulticastManager.monitor();

		verify(networkMulticastManager).startMdns(address);
	}

	@Test
	void monitor_shutsDownRemovedAddress() throws Exception {
		InetAddress address = InetAddress.getByName("192.168.1.144");
		Map<InetAddress, String> first = Map.of(address, "00:11:22:33:44:55");
		Map<InetAddress, String> second = Map.of();

		when(interfaces.getValidAddressesWithInterface()).thenReturn(first).thenReturn(second);
		when(jmDNSFactory.create(address)).thenReturn(jmDNS);

		networkMulticastManager.monitor(); // add
		networkMulticastManager.monitor(); // remove

		verify(networkMulticastManager).shutdownMdns(address);
	}

	@Test
	void monitor_doesNothingWhenNoChanges() throws Exception {
		InetAddress address = InetAddress.getByName("192.168.1.144");
		Map<InetAddress, String> map = Map.of(address, "00:11:22:33:44:55");

		when(interfaces.getValidAddressesWithInterface()).thenReturn(map).thenReturn(map);
		when(jmDNSFactory.create(address)).thenReturn(jmDNS);

		networkMulticastManager.monitor();
		networkMulticastManager.monitor();

		verify(networkMulticastManager, times(1)).startMdns(address);
		verify(networkMulticastManager, never()).shutdownMdns(any());
	}

	@Test
	void monitor_notifiesInfoProvider() throws Exception {
		when(interfaces.getValidAddressesWithInterface()).thenReturn(Collections.emptyMap());

		networkMulticastManager.monitor();

		verify(infoProvider).onChange(any());
	}

}
