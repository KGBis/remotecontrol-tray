package io.github.kgbis.remotecontrol.tray.net.actions;

import java.net.Socket;

public interface NetworkActionFactory {

	AckNetworkAction createAckAction(String[] remoteCommand, Socket socket);

	CancelShutdownNetworkAction createCancelShutdownAction(String[] remoteCommand, Socket socket);

	InfoNetworkAction createInfoAction(String[] remoteCommand, Socket socket);

	ShutdownNetworkAction createShutdownAction(String[] remoteCommand, Socket socket);

}
