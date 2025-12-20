/*
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
			case "ACK":
			default:
				return new AckNetworkAction(socket, remoteCommand);
		}
	}

}