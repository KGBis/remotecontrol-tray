/*
 * Remote PC Control
 * Copyright (C) 2026 Enrique García (https://github.com/KGBis)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.info;

import io.github.kgbis.remotecontrol.tray.ui.InformationScreen;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NetworkInfoProviderTest {

	@SuppressWarnings("unused")
	@Mock
	InformationScreen informationScreen;

	@InjectMocks
	NetworkInfoProvider networkInfoProvider;

	@Test
	void testGetMacAndIPv4Addresses() {
		Device device = Device.builder()
			.id(UUID.randomUUID())
			.hostname("Hostname")
			.deviceInfo(
					Device.DeviceInfo.builder().osName("Windows 11").osVersion("10.0").trayVersion("2026.01.1").build())
			.interfaces(Set.of(
					Device.DeviceInterface.builder()
						.ip("10.0.0.1")
						.mac("AA:AA:AA:AA:AA:AA")
						.port(6800)
						.type(Device.InterfaceType.ETHERNET)
						.build(),
					Device.DeviceInterface.builder()
						.ip("10.0.0.2")
						.mac("22:2A:00:2A:A2:22")
						.port(6800)
						.type(Device.InterfaceType.WIFI)
						.build()))
			.build();

		networkInfoProvider.onChange(device);

		assertNotNull(networkInfoProvider.getDevice());
		assertTrue(networkInfoProvider.getDevice()
			.getInterfaces()
			.stream()
			.anyMatch(iface -> iface.getMac().equals("AA:AA:AA:AA:AA:AA")));
		assertTrue(networkInfoProvider.getDevice()
			.getInterfaces()
			.stream()
			.anyMatch(iface -> iface.getIp().equals("10.0.0.2")));
		verify(informationScreen).onChange(any(Device.class));

	}

}
