package com.kikegg.remote.shutdown;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

	private static final String ACK = "primljeno"; // "received" in croatian

	private ServerSocket serverSocket;

	private final CMDWriter cmdWriter;

	public NetListener() {
		cmdWriter = new CMDWriter();
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
			// Wait to incomming messages (accept method is blocking)
			Socket socket = serverSocket.accept();
			log.info("Socket message received");

			// When server receives message, get message, send ack
			DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
			outToClient.writeBytes(ACK);
			log.info("Response ACK sent");

			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String clientMessage = inFromClient.readLine();
			log.info("Socket message read");

			cmdWriter.execShutdown(clientMessage);
		}
		catch (IOException e) {
			log.error("Socket error: {}", e.getMessage());
		}
	}

}
