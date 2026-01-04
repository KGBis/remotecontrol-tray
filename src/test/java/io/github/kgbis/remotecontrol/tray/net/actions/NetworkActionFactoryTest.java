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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@ExtendWith(MockitoExtension.class)
class NetworkActionFactoryTest {

	@SuppressWarnings("unused")
	@Mock
	NetworkInfoProvider networkInfoProvider;

	@Mock
	Socket socket;

	@InjectMocks
	NetworkActionFactory networkActionFactory;

	@Test
	void testCreateShutdownNetworkAction() {
		String[] remoteCommand = { "SHUTDOWN", "10", "MINUTES" };
		NetworkAction result = networkActionFactory.createAction(remoteCommand, socket, false);
		assertInstanceOf(ShutdownNetworkAction.class, result);
	}

	@Test
	void testCreateCancelShutdownNetworkAction() {
		String[] remoteCommand = { "CANCEL_SHUTDOWN" };
		NetworkAction result = networkActionFactory.createAction(remoteCommand, socket, false);
		assertInstanceOf(CancelShutdownNetworkAction.class, result);
	}

	@Test
	void testCreateInfoNetworkAction() {
		String[] remoteCommand = { "INFO", "10.0.0.1" };
		NetworkAction result = networkActionFactory.createAction(remoteCommand, socket, false);
		assertInstanceOf(InfoNetworkAction.class, result);
	}

	@Test
	void testCreateAckNetworkAction() {
		String[] remoteCommand = { "ACK" };
		NetworkAction result = networkActionFactory.createAction(remoteCommand, socket, false);
		assertInstanceOf(AckNetworkAction.class, result);
	}

	@Test
	void testCreateAckNetworkAction_withEmptyRemoteCommand() {
		String[] remoteCommand = {};
		NetworkAction result = networkActionFactory.createAction(remoteCommand, socket, false);
		assertInstanceOf(AckNetworkAction.class, result);
	}

}