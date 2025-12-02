package io.github.kgbis.remotecontrol.tray.net.actions;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.net.Socket;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetworkActionFactory {

	public static NetworkAction createAction(String[] remoteCommand, Socket socket, NetworkInfoProvider provider,
			boolean isDebug) {
		// TODO: See if "ACK" option is worth or better to reuse "INFO"
		if (ArrayUtils.isEmpty(remoteCommand)) {
			remoteCommand = new String[] { "ACK" };
		}

		switch (remoteCommand[0].toUpperCase()) {
			case "INFO":
				return new InfoNetworkAction(socket, remoteCommand, provider);
			case "SHUTDOWN":
				return new ShutdownNetworkAction(socket, remoteCommand, provider, isDebug);
			case "ACK":
			default:
				return new AckNetworkAction(socket, remoteCommand, provider);
		}
	}

}