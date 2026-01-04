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
package io.github.kgbis.remotecontrol.tray.net.actions;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.anyString;
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
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);
		when(networkInfoProvider.getMac(anyString())).thenReturn("00:AA:BB:CC:DD:EE");
		when(networkInfoProvider.getHostName(anyString())).thenReturn("192.168.1.100");
		when(networkInfoProvider.getIPv4Addresses()).thenReturn(List.of("192.168.1.100", "192.168.1.101"));

		infoNetworkAction = new InfoNetworkAction(socket, new String[] { "INFO", "192.168.1.100" },
				networkInfoProvider);
		infoNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String response = new String(inputStream.readAllBytes());
		assertEquals("192.168.1.100 00:AA:BB:CC:DD:EE", response.trim());
	}

	@Test
	void testExecute_IpIsNotRegistered() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(networkInfoProvider.getIPv4Addresses()).thenReturn(List.of("192.168.1.100", "192.168.1.101"));
		when(socket.getOutputStream()).thenReturn(outputStream);

		infoNetworkAction = new InfoNetworkAction(socket, new String[] { "INFO", "192.168.1.102" },
				networkInfoProvider);
		infoNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		assertEquals(42, inputStream.readAllBytes().length);
	}

	@Test
	void testParseArguments() {
		infoNetworkAction = new InfoNetworkAction(socket, new String[] { "INFO", "192.168.1.100" },
				networkInfoProvider);
		assertEquals("192.168.1.100", infoNetworkAction.parseArguments());
	}

	@Test
	void testParseArguments_invalidNumberOfArguments() {
		infoNetworkAction = new InfoNetworkAction(socket, new String[] { "INFO" }, networkInfoProvider);
		assertNull(infoNetworkAction.parseArguments());
	}

}
