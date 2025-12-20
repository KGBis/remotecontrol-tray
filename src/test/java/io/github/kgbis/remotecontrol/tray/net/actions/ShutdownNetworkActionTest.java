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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShutdownNetworkActionTest {

	@Mock
	Socket socket;

	ShutdownNetworkAction shutdownNetworkAction;

	@Test
	void testExecute() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);

		shutdownNetworkAction = new ShutdownNetworkAction(socket, new String[] { "SHUTDOWN", "10", "MINUTES" }, true);
		shutdownNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String response = new String(inputStream.readAllBytes());
		assertEquals("ACK", response.trim());
	}

	@Test
	void testExecute_wrongArguments_shouldNeverHappen() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);

		shutdownNetworkAction = new ShutdownNetworkAction(socket, new String[] { "SHUTDOWN", "10", "KILOS" }, true);
		shutdownNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String response = new String(inputStream.readAllBytes());
		assertEquals("ERROR invalid arguments", response.trim());
	}

	@Test
	void testParseArguments() {
		shutdownNetworkAction = new ShutdownNetworkAction(socket, new String[] { "SHUTDOWN", "10", "MINUTES" }, true);
		ShutdownNetworkActionData result = shutdownNetworkAction.parseArguments();
		Assertions.assertEquals(new ShutdownNetworkActionData(10, ChronoUnit.MINUTES), result);
	}

	@Test
	void testParseArguments_invalidNumberOfArguments() {
		shutdownNetworkAction = new ShutdownNetworkAction(socket, new String[] { "SHUTDOWN", "10" }, true);
		assertNull(shutdownNetworkAction.parseArguments());
	}

	@Test
	void testParseArguments_invalidArguments() {
		shutdownNetworkAction = new ShutdownNetworkAction(socket, new String[] { "SHUTDOWN", "10", "KILOS" }, true);
		assertNull(shutdownNetworkAction.parseArguments());
	}

}
