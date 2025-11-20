package com.kikegg.remote.pc.control.network;

import java.io.IOException;
import java.net.Socket;

public class ShutdownNetworkAction extends NetworkAction {

	public ShutdownNetworkAction(Socket socket, String[] args) {
		super(socket, args);
	}

	@Override
	public void execute() throws IOException {

	}

}
