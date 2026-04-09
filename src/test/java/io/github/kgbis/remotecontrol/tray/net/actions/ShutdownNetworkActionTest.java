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

import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.misc.RuntimeConfig;
import io.github.kgbis.remotecontrol.tray.ui.support.ActionDesktopNotifier;
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

	@Mock
	RuntimeConfig runtimeConfig;

	@Mock
	ConfigManager configManager;

	@Mock
	I18nService i18nService;

	@Mock
	ActionDesktopNotifier actionDesktopNotifier;

	ShutdownNetworkAction shutdownNetworkAction;

	@Test
	void testExecute() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);
		when(runtimeConfig.isDryRun()).thenReturn(true);

		shutdownNetworkAction = new ShutdownNetworkAction(runtimeConfig, actionDesktopNotifier, socket,
				new String[] { "SHUTDOWN", "10", "MINUTES" });
		shutdownNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String response = new String(inputStream.readAllBytes());
		assertEquals("ACK", response.trim());
	}

	@Test
	void testExecute_wrongArguments_shouldNeverHappen() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);

		shutdownNetworkAction = new ShutdownNetworkAction(runtimeConfig, actionDesktopNotifier, socket,
				new String[] { "SHUTDOWN", "10", "KILOS" });
		shutdownNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String response = new String(inputStream.readAllBytes());
		assertEquals("ERROR invalid arguments", response.trim());
	}

	@Test
	void testParseArguments() {
		shutdownNetworkAction = new ShutdownNetworkAction(runtimeConfig, actionDesktopNotifier, socket,
				new String[] { "SHUTDOWN", "10", "MINUTES" });
		ShutdownNetworkActionData result = shutdownNetworkAction.parseArguments();
		Assertions.assertEquals(new ShutdownNetworkActionData(10, ChronoUnit.MINUTES), result);
	}

	@Test
	void testParseArguments_invalidNumberOfArguments() {
		shutdownNetworkAction = new ShutdownNetworkAction(runtimeConfig, actionDesktopNotifier, socket,
				new String[] { "SHUTDOWN", "10" });
		assertNull(shutdownNetworkAction.parseArguments());
	}

	@Test
	void testParseArguments_invalidArguments() {
		shutdownNetworkAction = new ShutdownNetworkAction(runtimeConfig, actionDesktopNotifier, socket,
				new String[] { "SHUTDOWN", "10", "KILOS" });
		assertNull(shutdownNetworkAction.parseArguments());
	}

}
