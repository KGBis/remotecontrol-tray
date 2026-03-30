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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelShutdownNetworkActionTest {

	@Mock
	Socket socket;

	CancelShutdownNetworkAction cancelShutdownNetworkAction;

	@Test
	@EnabledOnOs({ OS.WINDOWS, OS.LINUX })
	void testExecute() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);

		cancelShutdownNetworkAction = new CancelShutdownNetworkAction(socket, new String[] { "CANCEL_SHUTDOWN" });
		cancelShutdownNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String response = new String(inputStream.readAllBytes());
		assertEquals("ACK", response.trim());
	}

	@Test
	@EnabledOnOs({ OS.WINDOWS, OS.LINUX })
	void testExecute_wrongArguments_shouldNeverHappen_butWorks() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);

		cancelShutdownNetworkAction = new CancelShutdownNetworkAction(socket,
				new String[] { "CANCEL_SHUTDOWN", "10", "KILOS" });
		cancelShutdownNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String response = new String(inputStream.readAllBytes());
		assertEquals("ACK", response.trim());
	}

	@Test
	void testParseArguments() {
		cancelShutdownNetworkAction = new CancelShutdownNetworkAction(socket, new String[] { "CANCEL_SHUTDOWN" });
		String[] parsedArguments = cancelShutdownNetworkAction.parseArguments();
		Assertions.assertEquals(2, parsedArguments.length);
	}

	@Test
	void testParseArguments_invalidNumberOfArguments() {
		cancelShutdownNetworkAction = new CancelShutdownNetworkAction(socket,
				new String[] { "CANCEL_SHUTDOWN", "NEVER", "HAPPENS" });
		String[] parsedArguments = cancelShutdownNetworkAction.parseArguments();
		Assertions.assertEquals(2, parsedArguments.length);
	}

}
