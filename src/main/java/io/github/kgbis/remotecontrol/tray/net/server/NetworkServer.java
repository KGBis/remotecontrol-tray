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
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NetworkServer {

	private final int port;

	// Fixed pool to avoid thread destruction/creation
	private final ExecutorService executor = Executors.newFixedThreadPool(2);

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

	public synchronized void start() throws IOException {
		if (running) {
			return;
		}

		running = true;

		serverSocket = new ServerSocket();
		// Enable SO_REUSEADDR socket option (important for Windows)
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new InetSocketAddress(port));
		serverSocket.setSoTimeout(1000);

		log.info("NetworkServer listening on port {}", port);

		serverThread = new Thread(this::socketLoop, "socket-thread");
		serverThread.start();
	}

	private void socketLoop() {
		while (running) {
			try {
				Socket socket = serverSocket.accept();
				log.debug("Connection accepted from {}", socket.getRemoteSocketAddress());

				executor.submit(() -> handleClient(socket));

			}
			catch (SocketTimeoutException ignored) {
				// just to re-evaluate "running" state
			}
			catch (SocketException e) {
				logSocketError(e);
			}
			catch (IOException e) {
				log.error("I/O error in accept()", e);
			}
		}

		log.debug("Server loop finished. Starting cleanup()");
		cleanup();
	}

	private void logSocketError(SocketException e) {
		if (running) {
			log.error("Socket exception (still running)", e);
		}
		else {
			log.debug("Socket closed during shutdown");
		}
	}

	public synchronized void stop() {
		if (!running) {
			return;
		}

		running = false;
		log.info("Stopping NetworkServer...");

		// 1) Close socket to wake up accept()
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		}
		catch (IOException e) {
			log.warn("Error closing ServerSocket", e);
		}

		// 2) Wait for server thread
		if (serverThread != null) {
			try {
				serverThread.join(2000);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.warn("Interrupted while joining serverThread");
			}
		}

		// 3) Apagar el executor
		executor.shutdown();
		try {
			if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
				log.warn("Executor did not terminate, forcing shutdownNow()");
				executor.shutdownNow();
			}
		}
		catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}

		log.info("NetworkServer stopped.");
	}

	private void cleanup() {
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		}
		catch (IOException e) {
			log.warn("Error during cleanup", e);
		}
	}

	private void handleClient(Socket socket) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			try {
				String message = br.readLine();
				log.info("Received message: {}", message);

				String[] args = StringUtils.split(message, " ");
				NetworkAction action = NetworkActionFactory.createAction(args, socket, networkInfoProvider, isDebug);

				action.execute();
			}
			finally {
				socket.close();
			}
		}
		catch (Exception e) {
			log.error("Error handling client", e);
		}
	}

	private void registerNetworkCallback(final NetworkChangeCallbackImpl networkChangeCallback) {
		final NetworkChangeListener listener = new NetworkChangeListener(1000);
		listener.addListener(networkChangeCallback);

		log.info("Starting NetworkChangeListener");
		listener.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.debug("ShutdownHook -> stopping listener and server");
			listener.removeListener(networkChangeCallback);
			listener.stop();

			try {
				NetworkServer.this.stop();
			}
			catch (Exception e) {
				log.warn("Error stopping from ShutdownHook. Nothing to worry about: {}", e.getMessage());
			}
		}, "ShutdownHook"));
	}

}
