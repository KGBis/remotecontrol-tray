/*
 * Copyright (c) Enrique García
 *
 * This file is part of RemoteControlTray.
 *
 * RemoteControlTray is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RemoteControlTray is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RemoteControlTray.  If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.mdns;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.internal.DeviceIdProvider;
import io.github.kgbis.remotecontrol.tray.net.internal.NetworkInterfaceProvider;
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
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetworkMulticastManagerTest {

	@Mock
	NetworkInterfaces interfaces;

	@Mock
	NetworkInterfaceProvider networkInterfaceProvider;

	@Mock
	JmDNS jmDNS;

	@Mock
	JmDNSFactory jmDNSFactory;

	@Mock
	NetworkInfoProvider infoProvider;

	@Mock
	DeviceIdProvider deviceIdProvider;

	@Spy
	@InjectMocks
	NetworkMulticastManager networkMulticastManager;

	@Test
	void start_callsRegister() throws IOException {
		InetAddress address = InetAddress.getByName("192.168.1.144");

		when(infoProvider.getHostName(any())).thenReturn("my-host");
		when(jmDNSFactory.create(any(InetAddress.class))).thenReturn(jmDNS);
		when(deviceIdProvider.getDeviceId()).thenReturn(UUID.randomUUID());

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getName()).thenReturn("wlan0");
		when(ni.getDisplayName()).thenReturn("Wi-Fi");
		when(networkInterfaceProvider.getByInetAddress(address)).thenReturn(ni);

		networkMulticastManager.startMdns(address);

		verify(jmDNS, times(1)).registerService(any());
	}

	@Test
	void stop_callsShutdownMdns() throws IOException {
		InetAddress address = InetAddress.getByName("192.168.1.144");

		when(jmDNSFactory.create(any(InetAddress.class))).thenReturn(jmDNS);
		when(infoProvider.getHostName(any())).thenReturn("my-host");
		when(deviceIdProvider.getDeviceId()).thenReturn(UUID.randomUUID());

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getName()).thenReturn("wlan0");
		when(ni.getDisplayName()).thenReturn("Wi-Fi");
		when(networkInterfaceProvider.getByInetAddress(address)).thenReturn(ni);

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
		when(infoProvider.getHostName(any())).thenReturn("my-host");
		when(deviceIdProvider.getDeviceId()).thenReturn(UUID.randomUUID());

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getName()).thenReturn("wlan0");
		when(ni.getDisplayName()).thenReturn("Wi-Fi");
		when(networkInterfaceProvider.getByInetAddress(address)).thenReturn(ni);

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
		when(infoProvider.getHostName(any())).thenReturn("my-host");
		when(deviceIdProvider.getDeviceId()).thenReturn(UUID.randomUUID());

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getName()).thenReturn("wlan0");
		when(ni.getDisplayName()).thenReturn("Wi-Fi");
		when(networkInterfaceProvider.getByInetAddress(address)).thenReturn(ni);

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
		when(infoProvider.getHostName(any())).thenReturn("my-host");
		when(deviceIdProvider.getDeviceId()).thenReturn(UUID.randomUUID());

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getName()).thenReturn("wlan0");
		when(ni.getDisplayName()).thenReturn("Wi-Fi");
		when(networkInterfaceProvider.getByInetAddress(address)).thenReturn(ni);

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
		when(infoProvider.getHostName(any())).thenReturn("my-host");
		when(deviceIdProvider.getDeviceId()).thenReturn(UUID.randomUUID());

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getName()).thenReturn("wlan0");
		when(ni.getDisplayName()).thenReturn("Wi-Fi");
		when(networkInterfaceProvider.getByInetAddress(address)).thenReturn(ni);

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
