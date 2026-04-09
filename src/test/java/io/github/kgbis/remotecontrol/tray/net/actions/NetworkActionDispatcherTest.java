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
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.ui.support.ActionDesktopNotifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SuppressWarnings("rawtypes")
@ExtendWith(MockitoExtension.class)
class NetworkActionDispatcherTest {

	@SuppressWarnings("unused")
	@Mock
	NetworkInfoProvider networkInfoProvider;

	@Mock
	ConfigManager configManager;

	@Mock
	I18nService i18nService;

	@Mock
	ActionDesktopNotifier actionDesktopNotifier;

	@Mock
	NetworkActionFactory networkActionFactory;

	@Mock
	Socket socket;

	@InjectMocks
	NetworkActionDispatcher networkActionDispatcher;

	@Test
	void testCreateShutdownNetworkAction() {
		String[] remoteCommand = { "SHUTDOWN", "10", "MINUTES" };
		when(networkActionFactory.createShutdownAction(any(), any(Socket.class)))
			.thenReturn(new ShutdownNetworkAction(new RuntimeConfig(), actionDesktopNotifier, socket, remoteCommand));
		NetworkAction result = networkActionDispatcher.createAction(remoteCommand, socket);
		assertInstanceOf(ShutdownNetworkAction.class, result);
	}

	@Test
	void testCreateCancelShutdownNetworkAction() {
		String[] remoteCommand = { "CANCEL_SHUTDOWN" };
		when(networkActionFactory.createCancelShutdownAction(any(), any(Socket.class)))
			.thenReturn(new CancelShutdownNetworkAction(/* configManager, i18nService, */ actionDesktopNotifier, socket,
					remoteCommand));

		NetworkAction result = networkActionDispatcher.createAction(remoteCommand, socket);
		assertInstanceOf(CancelShutdownNetworkAction.class, result);
	}

	@Test
	void testCreateInfoNetworkAction() {
		String[] remoteCommand = { "INFO", "10.0.0.1" };
		when(networkActionFactory.createInfoAction(any(), any(Socket.class)))
			.thenReturn(new InfoNetworkAction(networkInfoProvider, socket, remoteCommand));

		NetworkAction result = networkActionDispatcher.createAction(remoteCommand, socket);
		assertInstanceOf(InfoNetworkAction.class, result);
	}

	@Test
	void testCreateAckNetworkAction() {
		String[] remoteCommand = { "ACK" };
		when(networkActionFactory.createAckAction(any(), any(Socket.class)))
			.thenReturn(new AckNetworkAction(socket, remoteCommand));

		NetworkAction result = networkActionDispatcher.createAction(remoteCommand, socket);
		assertInstanceOf(AckNetworkAction.class, result);
	}

	@Test
	void testCreateAckNetworkAction_withEmptyRemoteCommand() {
		String[] remoteCommand = {};
		when(networkActionFactory.createAckAction(any(), any(Socket.class)))
			.thenReturn(new AckNetworkAction(socket, remoteCommand));

		NetworkAction result = networkActionDispatcher.createAction(remoteCommand, socket);
		assertInstanceOf(AckNetworkAction.class, result);
	}

}