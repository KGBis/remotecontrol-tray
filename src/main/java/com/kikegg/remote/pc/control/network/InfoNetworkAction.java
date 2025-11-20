package com.kikegg.remote.pc.control.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class InfoNetworkAction extends NetworkAction {

	public InfoNetworkAction(Socket socket, String[] args) {
		super(socket, args);
	}

	@Override
	public void execute() throws IOException {
		// "HOSTNAME MAC"
		String msg = String.format("%s %s", getLocalHostName(), "00:00:11:11:00:00");
		writeToSocket(socket, msg);
	}

	private String getLocalHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e) {
			return args[1]; // requested IP
		}
	}

}
