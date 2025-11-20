package com.kikegg.remote.pc.control.network;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

@Slf4j
public class InfoNetworkAction extends NetworkAction {

	public InfoNetworkAction(Socket socket, String[] args) {
		super(socket, args);
	}

	@Override
	public void execute() throws IOException {
		// check if IP argument is me
		List<String> iPv4Addresses = NetworkAction.getIPv4Addresses();
		if (!iPv4Addresses.contains(args[1])) {
			log.warn("Request contains an unknown IP. {} vs {}", args[1], iPv4Addresses);
			return;
		}

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
