/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.actions;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

@Slf4j
public class AckNetworkAction extends NetworkAction {

	public AckNetworkAction(Socket socket, String[] args) {
		super(socket, args);
	}

	@Override
	public void execute() throws IOException {
		log.info("ACK sent for args={}", (args != null ? Arrays.toString(args) : ""));
		writeToSocket(socket, "ACK");
	}

	@Override
	protected <T> T parseArguments() {
		return null;
	}

}
