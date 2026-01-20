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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelShutdownNetworkActionTest {

	@Mock
	Socket socket;

	CancelShutdownNetworkAction cancelShutdownNetworkAction;

	@Test
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
