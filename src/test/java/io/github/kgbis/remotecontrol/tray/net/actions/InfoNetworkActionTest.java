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
package io.github.kgbis.remotecontrol.tray.net.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kgbis.remotecontrol.tray.net.info.Device;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfoNetworkActionTest {

	@Mock
	Socket socket;

	@Mock
	NetworkInfoProvider networkInfoProvider;

	InfoNetworkAction infoNetworkAction;

	@Test
	void testExecute() throws IOException {
		Device device = Device.builder()
			.id(UUID.randomUUID())
			.hostname("Hostname")
			.deviceInfo(
					Device.DeviceInfo.builder().osName("Windows 11").osVersion("6.0").trayVersion("2026.01.1").build())
			.interfaces(Set.of(
					Device.DeviceInterface.builder()
						.ip("192.168.1.100")
						.mac("00:AA:BB:CC:DD:EE")
						.port(6800)
						.type(Device.InterfaceType.ETHERNET)
						.build(),
					Device.DeviceInterface.builder()
						.ip("192.168.1.101")
						.mac("22:2A:2A:A2:22")
						.port(6800)
						.type(Device.InterfaceType.ETHERNET)
						.build()))
			.build();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);
		when(networkInfoProvider.getDevice()).thenReturn(device);

		infoNetworkAction = new InfoNetworkAction(networkInfoProvider, socket,
				new String[] { "INFO", "192.168.1.100" });
		infoNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String response = new String(inputStream.readAllBytes());
		assertEquals(new ObjectMapper().writeValueAsString(device), response.trim());
	}

	@Test
	void testExecute_IpIsNotRegistered() throws IOException {
		Device device = Device.builder()
			.id(UUID.randomUUID())
			.hostname("Hostname")
			.deviceInfo(
					Device.DeviceInfo.builder().osName("Windows 11").osVersion("10.0").trayVersion("2026.01.1").build())
			.interfaces(Set.of(
					Device.DeviceInterface.builder()
						.ip("10.0.0.1")
						.mac("AA:AA:AA:AA:AA")
						.port(6800)
						.type(Device.InterfaceType.ETHERNET)
						.build(),
					Device.DeviceInterface.builder()
						.ip("10.0.0.2")
						.mac("22:2A:2A:A2:22")
						.port(6800)
						.type(Device.InterfaceType.WIFI)
						.build()))
			.build();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		when(socket.getOutputStream()).thenReturn(outputStream);
		when(networkInfoProvider.getDevice()).thenReturn(device);

		infoNetworkAction = new InfoNetworkAction(networkInfoProvider, socket,
				new String[] { "INFO", "192.168.1.102" });
		infoNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		assertEquals(42, inputStream.readAllBytes().length);
	}

	@Test
	void testParseArguments() {
		infoNetworkAction = new InfoNetworkAction(networkInfoProvider, socket,
				new String[] { "INFO", "192.168.1.100" });
		assertEquals("192.168.1.100", infoNetworkAction.parseArguments());
	}

	@Test
	void testParseArguments_invalidNumberOfArguments() {
		infoNetworkAction = new InfoNetworkAction(networkInfoProvider, socket, new String[] { "INFO" });
		assertNull(infoNetworkAction.parseArguments());
	}

}
