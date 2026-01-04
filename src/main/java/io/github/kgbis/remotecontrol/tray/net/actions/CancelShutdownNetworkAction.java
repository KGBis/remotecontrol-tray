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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CancelShutdownNetworkAction extends NetworkAction<String[]> {

	protected CancelShutdownNetworkAction(Socket socket, String[] args) {
		super(socket, args);
	}

	@Override
	public void execute() throws IOException {
		String[] cmdLine = parseArguments();
		log.info("Executing cancel shutdown -> {}", StringUtils.join(cmdLine, " "));
		int exitCode = execute(cmdLine);
		writeToSocket(socket, exitCode == 0 ? "ACK" : "ERROR " + exitCode);
	}

	@Override
	protected String[] parseArguments() {
		List<String> cmd = new ArrayList<>();
		cmd.add("shutdown");

		if (SystemUtils.IS_OS_WINDOWS) {
			cmd.add("-a");
		}
		else {
			cmd.add("-c");
		}

		return cmd.toArray(new String[0]);
	}

}
