package com.kikegg.remote.pc.control.network;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
@RequiredArgsConstructor
public abstract class NetworkAction {

	protected final Socket socket;

	protected final String[] args;

	public abstract void execute() throws IOException;

	void writeToSocket(Socket socket, String message) throws IOException {
		DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
		outToClient.writeBytes(message + "\n");
        outToClient.flush();
		log.info("Response sent: {}", message);
	}

}
