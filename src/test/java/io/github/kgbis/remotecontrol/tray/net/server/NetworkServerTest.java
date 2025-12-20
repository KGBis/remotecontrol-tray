/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.server;

import io.github.kgbis.remotecontrol.tray.net.actions.NetworkAction;
import io.github.kgbis.remotecontrol.tray.net.actions.NetworkActionFactory;
import io.github.kgbis.remotecontrol.tray.net.mdns.NetworkMulticastManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetworkServerTest {

	@Mock
	ServerSocketFactory socketFactory;

	@Mock
	NetworkActionFactory networkActionFactory;

	@Mock
	ServerSocket serverSocket;

	@Mock
	Socket socket;

	@Mock
	ServerLoopRunner loopRunner;

	@Mock
	NetworkMulticastManager networkMulticastManager;

	@InjectMocks
	NetworkServer networkServer;

	@Test
	void startShouldStartLoope() throws Exception {
		when(socketFactory.create()).thenReturn(serverSocket);
		doNothing().when(serverSocket).setReuseAddress(anyBoolean());
		doNothing().when(serverSocket).bind(any());
		doNothing().when(serverSocket).setSoTimeout(anyInt());

		networkServer.start();

		verify(loopRunner).start(any());
	}

	@Test
	void startShouldBeIdempotent() throws Exception {
		when(socketFactory.create()).thenReturn(serverSocket);

		networkServer.start();
		networkServer.start();

		verify(socketFactory, times(1)).create();
		verify(loopRunner, times(1)).start(any());
	}

	@Test
	void startAndStopShouldWorkCleanly() throws Exception {
		when(socketFactory.create()).thenReturn(serverSocket);

		networkServer.start();
		networkServer.stop();

		verify(loopRunner).start(any());
		verify(loopRunner).stop();
		verify(networkMulticastManager).stop();
	}

	@Test
	void stopShouldBeIdempotent() {
		assertDoesNotThrow(networkServer::stop);
		assertDoesNotThrow(networkServer::stop);
	}

	@Test
	void stopShouldCloseSocket() throws Exception {
		when(socketFactory.create()).thenReturn(serverSocket);

		networkServer.start();
		networkServer.stop();

		verify(serverSocket).close();
	}

	@Test
	void shouldCreateAndExecuteNetworkActionForValidCommand() throws Exception {
		String[] remoteCommand = { "INFO", "192.168.1.111" };

		NetworkAction action = mock(NetworkAction.class);

		when(socket.getInputStream()).thenReturn(new ByteArrayInputStream("INFO 192.168.1.111\n".getBytes()));
		when(networkActionFactory.createAction(remoteCommand, socket, false)).thenReturn(action);

		networkServer.handleClient(socket);

		verify(networkActionFactory).createAction(remoteCommand, socket, false);
		verify(action).execute();
	}

}