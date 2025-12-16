package io.github.kgbis.remotecontrol.tray.net.actions;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public abstract class NetworkAction {

	protected final Socket socket;

	protected final String[] args;

	protected final NetworkInfoProvider networkInfoProvider;

	protected NetworkAction(Socket socket, String[] args, NetworkInfoProvider networkInfoProvider) {
		this.socket = socket;
		this.args = args;
		this.networkInfoProvider = networkInfoProvider;
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
