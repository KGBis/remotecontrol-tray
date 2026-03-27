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

import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.ArrayUtils;

import java.net.Socket;

@Singleton
public class NetworkActionFactory {

	private final NetworkInfoProvider networkInfoProvider;

	@Inject
	public NetworkActionFactory(NetworkInfoProvider networkInfoProvider) {
		this.networkInfoProvider = networkInfoProvider;
	}

	@SuppressWarnings("rawtypes")
	public NetworkAction createAction(String[] remoteCommand, Socket socket, boolean isDryRun) {
		// When mobile app "pings", it sends no command. Replace with ACK
		if (ArrayUtils.isEmpty(remoteCommand)) {
			remoteCommand = new String[] { "ACK" };
		}

		return switch (remoteCommand[0].toUpperCase()) {
			case "INFO" -> new InfoNetworkAction(socket, remoteCommand, networkInfoProvider);
			case "SHUTDOWN" -> new ShutdownNetworkAction(socket, remoteCommand, isDryRun);
			case "CANCEL_SHUTDOWN" -> new CancelShutdownNetworkAction(socket, remoteCommand);
			default -> new AckNetworkAction(socket, remoteCommand);
		};
	}

}