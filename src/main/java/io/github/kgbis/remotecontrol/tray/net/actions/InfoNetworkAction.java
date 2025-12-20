/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.actions;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
public class InfoNetworkAction extends NetworkAction {

	private final NetworkInfoProvider provider;

	public InfoNetworkAction(Socket socket, String[] args, NetworkInfoProvider networkInfoProvider) {
		super(socket, args);
		this.provider = networkInfoProvider;
	}

	@Override
	public void execute() throws IOException {
		String ip = parseArguments();

		if (!provider.getIPv4Addresses().contains(ip)) {
			log.warn("Unknown IP requested: {}", ip);
			return;
		}

		String msg = provider.getHostName(ip) + " " + provider.getMac(ip);
		log.info("Responding with: {}", msg);

		OutputStream out = socket.getOutputStream();
		out.write((msg + "\n").getBytes());
		out.flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String parseArguments() {
		if (args.length < 2)
			return null;
		return args[1];
	}

}
