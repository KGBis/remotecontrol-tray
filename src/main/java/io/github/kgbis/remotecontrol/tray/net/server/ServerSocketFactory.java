package io.github.kgbis.remotecontrol.tray.net.server;

import java.io.IOException;
import java.net.ServerSocket;

public interface ServerSocketFactory {

	ServerSocket create() throws IOException;

}
