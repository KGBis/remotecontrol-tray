package io.github.kgbis.remotecontrol.tray.net.server;

import jakarta.inject.Singleton;

import java.io.IOException;
import java.net.ServerSocket;

@Singleton
public class DefaultServerSocketFactory implements ServerSocketFactory {

	@Override
	public ServerSocket create() throws IOException {
		return new ServerSocket();
	}

}
