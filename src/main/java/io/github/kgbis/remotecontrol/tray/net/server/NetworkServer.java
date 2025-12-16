package io.github.kgbis.remotecontrol.tray.net.server;

import io.github.kgbis.remotecontrol.tray.cli.CliArguments;
import io.github.kgbis.remotecontrol.tray.net.actions.NetworkAction;
import io.github.kgbis.remotecontrol.tray.net.actions.NetworkActionFactory;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeListener;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeRegistrar;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.mdns.MulticastServiceRegistar;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
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

	private final ServerSocketFactory socketFactory;

	private final ServerLoopRunner loopRunner;

	private final NetworkInfoProvider networkInfoProvider;

	private final NetworkActionFactory networkActionFactory;

	private final MulticastServiceRegistar multicastServiceRegistar;

	private volatile boolean running = false;

	private boolean isDryRun = false;

	private ServerSocket serverSocket;

	private final NetworkChangeRegistrar networkChangeRegistrar;

	@Inject
	public NetworkServer(ServerSocketFactory socketFactory, ServerLoopRunner loopRunner,
			NetworkInfoProvider networkInfoProvider, NetworkActionFactory networkActionFactory,
			MulticastServiceRegistar multicastServiceRegistar, NetworkChangeRegistrar networkChangeRegistrar) {
		this.socketFactory = socketFactory;
		this.loopRunner = loopRunner;
		this.networkInfoProvider = networkInfoProvider;
		this.networkActionFactory = networkActionFactory;
		this.multicastServiceRegistar = multicastServiceRegistar;
		this.networkChangeRegistrar = networkChangeRegistrar;
	}

	public NetworkServer arguments(CliArguments args) {
		isDryRun = args.isDryRun();
		if (args.isDryRun()) {
			log.debug("Executing in DryRun mode. No shutdown will be performed!");
		}
		return this;
	}

	public synchronized void start() throws IOException, InterruptedException {
		// register network interfaces listener
		registerNetworkListener(networkInfoProvider.getNetworkChangeListener());

		// wait until NetworkInfoProvider has initialized
		networkInfoProvider.awaitInitialization();

		// register shutdown hook
		registerShutdownHook();

		// Already running. Don't want to start again
		if (running) {
			return;
		}

		running = true;

		serverSocket = socketFactory.create();
		// Enable SO_REUSEADDR socket option (important for Windows)
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new InetSocketAddress(PORT));
		serverSocket.setSoTimeout(POLL_INTERVAL_MS);

		log.info("NetworkServer listening on port {}", PORT);

		// Start main socket loop
		loopRunner.start(this::socketLoop);
	}

	public synchronized void stop() {
		if (!running) {
			return;
		}

		running = false;
		log.info("Stopping NetworkServer...");

		// Stop listener
		if (networkChangeRegistrar != null) {
			networkChangeRegistrar.removeListener(networkInfoProvider.getNetworkChangeListener());
			networkChangeRegistrar.stop();
		}

		closeSocket();

		// Wait for server loop thread to stop
		try {
			loopRunner.stop();
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.warn("Interrupted while terminating socket-thread");
		}

		// Executor shutdown
		executor.shutdown();
		try {
			if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
				log.warn("Executor did not terminate, forcing shutdownNow()");
				executor.shutdownNow();
			}
		}
		catch (InterruptedException e) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}

		// mDNS service shutdown
		multicastServiceRegistar.unregister();

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
				log.debug("Server socket closed");
			}
		}
		catch (IOException e) {
			log.warn("Error closing ServerSocket", e);
		}
	}

	void handleClient(Socket socket) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
			try (socket) {
				String message = br.readLine();
				log.info("Received message: {}", message);

				String[] args = StringUtils.split(message, " ");
				NetworkAction action = networkActionFactory.createAction(args, socket, isDryRun);

				action.execute();
			}
		}
		catch (Exception e) {
			log.error("Error handling client", e);
		}
	}

	private void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.debug("Shutdown hook triggered");
			try {
				NetworkServer.this.stop();
			}
			catch (Exception e) {
				log.warn("Error stopping server from ShutdownHook. Nothing to worry about", e);
			}
		}, "shutdown-hook"));
	}

	private void registerNetworkListener(final NetworkChangeListener networkChangeListener) {
		networkChangeRegistrar.addListener(networkChangeListener);

		log.info("Listening for network interfaces changes");
		networkChangeRegistrar.start();
	}

}
