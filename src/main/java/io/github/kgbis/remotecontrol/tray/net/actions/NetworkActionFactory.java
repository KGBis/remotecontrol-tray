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

	public NetworkAction createAction(String[] remoteCommand, Socket socket, boolean isDryRun) {
		// See if "ACK" option is worth or better to reuse "INFO"
		if (ArrayUtils.isEmpty(remoteCommand)) {
			remoteCommand = new String[] { "ACK" };
		}

		switch (remoteCommand[0].toUpperCase()) {
			case "INFO":
				return new InfoNetworkAction(socket, remoteCommand, networkInfoProvider);
			case "SHUTDOWN":
				return new ShutdownNetworkAction(socket, remoteCommand, isDryRun);
			case "CANCEL_SHUTDOWN":
				return new CancelShutdownNetworkAction(socket, remoteCommand);
			case "ACK":
			default:
				return new AckNetworkAction(socket, remoteCommand);
		}
	}

}