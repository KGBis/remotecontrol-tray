/*
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

		infoNetworkAction = new InfoNetworkAction(socket, new String[] { "INFO", "192.168.1.102" },
				networkInfoProvider);
		infoNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		assertEquals(0, inputStream.readAllBytes().length);
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
