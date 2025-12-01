package com.kikegg.remote.pc.control.network.server;

import com.kikegg.remote.pc.control.network.actions.NetworkAction;
import com.kikegg.remote.pc.control.network.actions.NetworkActionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NetworkServer {

	private final int port;

	private final ExecutorService executor = Executors.newCachedThreadPool();

	private final NetworkInfoProvider networkInfoProvider;

	private volatile boolean running = false;

	private boolean isDebug = false;

	public NetworkServer(int port, NetworkInfoProvider networkInfoProvider) {
		this.port = port;
		this.networkInfoProvider = networkInfoProvider;
	}

    public NetworkServer setTest(String[] args) {
		if (ArrayUtils.isNotEmpty(args) && "--isDebug".equalsIgnoreCase(args[0])) {
			isDebug = true;
			log.debug("Executing in debug mode. No shutdown will be performed!");
		}
		return this;
	}

	@SuppressWarnings("resource")
    public void start() throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		running = true;
		log.info("NetworkServer listening on port {}", port);

		while (running) {
			Socket socket = serverSocket.accept();
			executor.submit(() -> handleClient(socket));
		}
	}

	private void handleClient(Socket socket) {
		try {
			String message = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
			log.info("Received message: {}", message);

			String[] args = message.split(" ");
			NetworkAction action = NetworkActionFactory.createAction(args, socket, networkInfoProvider, isDebug);
			if (action != null)
				action.execute();
		}
		catch (Exception e) {
			log.error("Error handling client", e);
		}
		finally {
			try {
				socket.close();
			}
			catch (IOException ignored) {
                // ignored
			}
		}
	}

}
