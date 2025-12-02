package io.github.kgbis.remotecontrol.tray.net.server;

import io.github.kgbis.remotecontrol.tray.net.actions.NetworkAction;
import io.github.kgbis.remotecontrol.tray.net.actions.NetworkActionFactory;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeCallbackImpl;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeListener;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class NetworkServer {

	private final int port;

	private final ExecutorService executor = Executors.newCachedThreadPool();

	private final NetworkInfoProvider networkInfoProvider;

	private volatile boolean running = false;

	private boolean isDebug = false;

	private ServerSocket serverSocket;

	private Thread serverThread;

	public NetworkServer(int port, NetworkInfoProvider networkInfoProvider) {
		this.port = port;
		this.networkInfoProvider = networkInfoProvider;
		registerNetworkCallback(networkInfoProvider.getCallback());
	}

	public NetworkServer setTest(String[] args) {
		if (ArrayUtils.isNotEmpty(args) && "--isDebug".equalsIgnoreCase(args[0])) {
			isDebug = true;
			log.debug("Executing in debug mode. No shutdown will be performed!");
		}
		return this;
	}

	public void start() throws IOException {
		if (running) {
			return; // avoid a restart
		}

		running = true;

		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(1000); // 1 second to check if running
		log.info("NetworkServer listening on port {}", port);

		serverThread = new Thread(() -> {
			while (running) {
				try {
					Socket socket = serverSocket.accept();
					executor.submit(() -> handleClient(socket));
				}
				catch (SocketTimeoutException ignored) {
					// Timeout to check "running"
				}
				catch (IOException e) {
					if (running) {
						log.error("Error accepting connection", e);
					}
				}
			}
			cleanup();
		}, "socket-thread");

		serverThread.start();
	}

	@SuppressWarnings("unused")
	public void stop() {
		running = false;
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close(); // force accept() to throw IOException
			}
			if (serverThread != null) {
				serverThread.join(); // wait to end
			}
			log.info("NetworkServer stopped.");
		}
		catch (IOException | InterruptedException e) { // NOSONAR
			log.error("Error stopping server", e);
		}
	}

	private void cleanup() {
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		}
		catch (IOException e) {
			log.error("Error during cleanup", e);
		}
	}

	private void handleClient(Socket socket) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			String message = br.readLine();
			log.info("Received message: {}", message);

			String[] args = StringUtils.split(message, " ");
			NetworkAction action = NetworkActionFactory.createAction(args, socket, networkInfoProvider, isDebug);
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

	private void registerNetworkCallback(NetworkChangeCallbackImpl networkChangeCallback) {
		NetworkChangeListener listener = new NetworkChangeListener(1000);
		listener.addListener(networkChangeCallback);

		log.info("Starting NetworkChangeListener");
		listener.start();

		// register shutdown hook to remove listener daemon
		log.info("Registering shutdown hook");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.debug("Removing networkChangeCallback listener");
			listener.removeListener(networkChangeCallback);
			listener.stop();
			log.debug("Stopping NetworkServer");
			stop();
		}, "ShutdownHook"));
	}

}
