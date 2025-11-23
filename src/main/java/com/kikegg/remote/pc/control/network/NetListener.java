package com.kikegg.remote.pc.control.network;

import com.kikegg.remote.pc.control.model.Action;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class NetListener implements Runnable {

	private static final int PORT = 6800;

	private ServerSocket serverSocket;

	public NetListener() {
		Runtime.getRuntime().addShutdownHook(new Thread(this::cleanUpOnSuspend));
	}

	@SuppressWarnings("InfiniteLoopStatement")
	public void listen() throws IOException, ExecutionException, InterruptedException {
		// Initialize
		serverSocket = new ServerSocket(PORT);
		ExecutorService executor = Executors.newSingleThreadExecutor();

		// Result from threaded execution is condition in this while
		// While the param string received from socket is blank
		while (true) {
			log.info("Socket listening @ port " + PORT);
			Future<?> submit = executor.submit(this);
			submit.get();
		}
	}

	@Override
	public void run() {
		try {
			// Wait to incoming messages (accept method is blocking)
			Socket socket = serverSocket.accept();
			log.info("Socket message received");

			// Read client input (if any)
			String inMessage = getIncomingMessage(socket);

			// execute action
			execute(socket, inMessage);
		}
		catch (IOException e) {
			log.error("Socket error: {}", e.getMessage());
		}
	}

	private void execute(Socket socket, String inMessage) throws IOException {
		String[] arguments = StringUtils.split(inMessage);

		Action action = Action.ACK;
		try {
			if (arguments != null) {
				action = Action.valueOf(arguments[0]);
			}
		}
		catch (Exception e) {
			log.error("Invalid action {}", arguments[0]);
			return;
		}

		NetworkAction netAction;
		switch (action) {
			case SHUTDOWN:
				netAction = new ShutdownNetworkAction(socket, arguments);
				break;
			case INFO:
				netAction = new InfoNetworkAction(socket, arguments);
				break;
			default:
				netAction = new AckNetworkAction(socket, arguments);
		}

		netAction.execute();
	}

	private String getIncomingMessage(Socket socket) throws IOException {
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String clientMessage = inFromClient.readLine();
		log.info("Socket message read: {}", clientMessage);
		return clientMessage;
	}

	private void cleanUpOnSuspend() {
		if (serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
				log.info("Socket being closed for suspension/hibernanion");
			}
			catch (IOException e) {
				log.error("Error:", e);
			}
		}
	}

}
