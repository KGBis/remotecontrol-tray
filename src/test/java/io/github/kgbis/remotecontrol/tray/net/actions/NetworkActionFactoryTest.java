/*
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