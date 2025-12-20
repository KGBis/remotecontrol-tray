/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.actions;

import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public abstract class NetworkAction {

	protected final Socket socket;

	protected final String[] args;

	protected NetworkAction(Socket socket, String[] args) {
		this.socket = socket;
		this.args = args;
	}

	public abstract void execute() throws IOException;

	protected abstract <T> T parseArguments();

	void writeToSocket(Socket socket, String message) throws IOException {
		DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
		outToClient.writeBytes(message + "\n");
		outToClient.flush();
		log.info("Response sent: {}", message);
	}

}
