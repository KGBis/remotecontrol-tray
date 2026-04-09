/*
 * Remote PC Control
 * Copyright (C) 2026 Enrique García (https://github.com/KGBis)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.net.actions;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.ArrayUtils;

import java.net.Socket;
import java.util.Locale;

@Singleton
public class NetworkActionDispatcher {

	private final NetworkActionFactory networkActionFactory;

	@Inject
	public NetworkActionDispatcher(NetworkActionFactory networkActionFactory) {
		this.networkActionFactory = networkActionFactory;
	}

	@SuppressWarnings("rawtypes")
	public NetworkAction createAction(String[] remoteCommand, Socket socket) {
		// When mobile app "pings", it sends no command. Replace with ACK
		if (ArrayUtils.isEmpty(remoteCommand)) {
			remoteCommand = new String[] { "ACK" };
		}

		String command = remoteCommand[0].toUpperCase(Locale.ROOT);

		return switch (command) {
			case "INFO" -> networkActionFactory.createInfoAction(remoteCommand, socket);
			case "SHUTDOWN" -> networkActionFactory.createShutdownAction(remoteCommand, socket);
			case "CANCEL_SHUTDOWN" -> networkActionFactory.createCancelShutdownAction(remoteCommand, socket);
			default -> networkActionFactory.createAckAction(remoteCommand, socket);
		};
	}

}