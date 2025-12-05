package io.github.kgbis.remotecontrol.tray.net.server;

import com.google.inject.Inject;
import io.github.kgbis.remotecontrol.tray.cli.CliArguments;
import io.github.kgbis.remotecontrol.tray.net.actions.NetworkAction;
import io.github.kgbis.remotecontrol.tray.net.actions.NetworkActionFactory;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeListener;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeRegistrar;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
@Slf4j
public class NetworkServer {

	public static final int PORT = 6800;

	public static final int POLL_INTERVAL_MS = 1000;

	// Fixed pool to avoid thread destruction/creation
	private final ExecutorService executor = Executors.newFixedThreadPool(2);

	private final NetworkInfoProvider networkInfoProvider;

	private volatile boolean running = false;

	private Boolean isDryRun;

	private ServerSocket serverSocket;

	private Thread serverThread;

    @Inject
	public NetworkServer(NetworkInfoProvider networkInfoProvider) {
		this.networkInfoProvider = networkInfoProvider;
        registerNetworkListener(networkInfoProvider.getNetworkChangeListener());
	}

	public NetworkServer arguments(CliArguments args) {
		isDryRun = args.isDryRun();
		if (args.isDryRun()) {
			log.debug("Executing in DryRun mode. No shutdown will be performed!");
		}
		return this;
	}

	public synchronized void start() throws IOException, InterruptedException {
		// wait until NetworkInfoProvider has initialized
        networkInfoProvider.awaitInitialization();

		// Already running. Don't want to start again
		if (running) {
			return;
		}

		running = true;

		serverSocket = new ServerSocket();
		// Enable SO_REUSEADDR socket option (important for Windows)
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new InetSocketAddress(PORT));
		serverSocket.setSoTimeout(POLL_INTERVAL_MS);

		log.info("NetworkServer listening on port {}", PORT);

		serverThread = new Thread(this::socketLoop, "socket-thread");
		serverThread.start();
	}

	public synchronized void stop() {
		if (!running) {
			return;
		}

		running = false;
		log.info("Stopping NetworkServer...");

		// 1) Close socket to wake up accept()
		closeSocket();

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

		// 3) Executor shutdown
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

	/* private methods */

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
		closeSocket();
	}

	private void logSocketError(SocketException e) {
		if (running) {
			log.error("Socket exception (still running)", e);
		}
		else {
			log.debug("Socket closed during shutdown");
		}
	}

	private void closeSocket() {
		try {
			if (serverSocket != null && !serverSocket.isClosed()) {
				serverSocket.close();
			}
		}
		catch (IOException e) {
			log.warn("Error closing ServerSocket", e);
		}
	}

	private void handleClient(Socket socket) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			try {
				String message = br.readLine();
				log.info("Received message: {}", message);

				String[] args = StringUtils.split(message, " ");
				NetworkAction action = NetworkActionFactory.createAction(args, socket, networkInfoProvider, isDryRun);

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

	private void registerNetworkListener(final NetworkChangeListener networkChangeListener) {
		final NetworkChangeRegistrar listener = new NetworkChangeRegistrar(POLL_INTERVAL_MS);
		listener.addListener(networkChangeListener);

		log.info("Listening for network interfaces changes");
		listener.start();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.debug("Stopping listener and server");
			listener.removeListener(networkChangeListener);
			listener.stop();

			try {
				NetworkServer.this.stop();
			}
			catch (Exception e) {
				log.warn("Error stopping from ShutdownHook. Nothing to worry about: {}", e.getMessage());
			}
		}, "shutdown-hook"));
	}

}
