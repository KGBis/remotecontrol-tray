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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
public abstract class NetworkAction<R> {

	protected final Socket socket;

	protected final String[] args;

	protected NetworkAction(Socket socket, String[] args) {
		this.socket = socket;
		this.args = args;
	}

	public abstract void execute() throws IOException;

	protected abstract R parseArguments();

	protected int execute(String[] cmdLine) {
		@SuppressWarnings("ConfusingArgumentToVarargsMethod")
		String strCommandLine = StringUtils.joinWith(" ", cmdLine);

		int exitCode = 0;

		try {
			ProcessBuilder builder = new ProcessBuilder(cmdLine);

			// Redirect the error stream to the output stream to make all output captured
			builder.redirectErrorStream(true);
			Process process = builder.start();

			// Get the input stream (includes standard error by redirectErrorStream(true))
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					log.debug(line);
				}
			}

			// Wait for the process to complete and get the exit code
			exitCode = process.waitFor();
			log.debug("shutdown exit code: {}", exitCode);
		}
		catch (IOException e) {
			log.error("Error executing '{}' command", strCommandLine, e);
		}
		catch (InterruptedException e) {
			log.error("Error executing '{}' command", strCommandLine, e);
			Thread.currentThread().interrupt();
		}

		return exitCode;
	}

	void writeToSocket(Socket socket, String message) throws IOException {
		DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
		outToClient.writeBytes(message + "\n");
		outToClient.flush();
		log.info("Response sent: {}", message);
	}

}
