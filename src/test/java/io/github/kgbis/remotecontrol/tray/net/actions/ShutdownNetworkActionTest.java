package io.github.kgbis.remotecontrol.tray.net.actions;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
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
	NetworkInfoProvider networkInfoProvider;

	ShutdownNetworkAction shutdownNetworkAction;

	@Test
	void testExecute() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);

		shutdownNetworkAction = new ShutdownNetworkAction(socket, new String[] { "SHUTDOWN", "10", "MINUTES" },
				networkInfoProvider, true);
		shutdownNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String response = new String(inputStream.readAllBytes());
		assertEquals("ACK", response.trim());
	}

	@Test
	void testExecute_wrongArguments_shouldNeverHappen() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		when(socket.getOutputStream()).thenReturn(outputStream);

		shutdownNetworkAction = new ShutdownNetworkAction(socket, new String[] { "SHUTDOWN", "10", "KILOS" },
				networkInfoProvider, true);
		shutdownNetworkAction.execute();

		InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		String response = new String(inputStream.readAllBytes());
		assertEquals("ERROR invalid arguments", response.trim());
	}

	@Test
	void testParseArguments() {
		shutdownNetworkAction = new ShutdownNetworkAction(socket, new String[] { "SHUTDOWN", "10", "MINUTES" },
				networkInfoProvider, true);
		ShutdownNetworkActionData result = shutdownNetworkAction.parseArguments();
		Assertions.assertEquals(new ShutdownNetworkActionData(10, ChronoUnit.MINUTES), result);
	}

	@Test
	void testParseArguments_invalidNumberOfArguments() {
		shutdownNetworkAction = new ShutdownNetworkAction(socket, new String[] { "SHUTDOWN", "10" },
				networkInfoProvider, true);
		assertNull(shutdownNetworkAction.parseArguments());
	}

	@Test
	void testParseArguments_invalidArguments() {
		shutdownNetworkAction = new ShutdownNetworkAction(socket, new String[] { "SHUTDOWN", "10", "KILOS" },
				networkInfoProvider, true);
		assertNull(shutdownNetworkAction.parseArguments());
	}

}
