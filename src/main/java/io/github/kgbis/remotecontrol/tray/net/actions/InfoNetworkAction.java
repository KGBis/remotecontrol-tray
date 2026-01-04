/*
 * Copyright (c) Enrique García
 *
 * This file is part of RemoteControlTray.
 *
 * RemoteControlTray is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RemoteControlTray is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RemoteControlTray.  If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.actions;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
public class InfoNetworkAction extends NetworkAction<String> {

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
			writeToSocket(socket, "ERROR Unknown IP requested: " + ip);
			return;
		}

		String msg = provider.getHostName(ip) + " " + provider.getMac(ip);
		log.info("Responding with: {}", msg);
		writeToSocket(socket, msg);
	}

	@Override
	protected String parseArguments() {
		if (args.length < 2)
			return null;
		return args[1];
	}

}
