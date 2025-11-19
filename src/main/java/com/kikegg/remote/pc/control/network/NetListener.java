package com.kikegg.remote.pc.control.network;

import com.kikegg.remote.pc.control.CMDWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
public class NetListener implements Runnable {

	private static final int PORT = 6800;

	private static final String ACK = "ACK";

	private ServerSocket serverSocket;

	private final CMDWriter cmdWriter;

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

			if (StringUtils.isBlank(inMessage)) {
				sendAck(socket);
				return;
			}

			/*
			 * BufferedReader inFromClient = new BufferedReader(new
			 * InputStreamReader(socket.getInputStream())); String clientMessage =
			 * inFromClient.readLine(); log.info("Socket message read");
			 */

			// When server receives message, get message, send ack

			cmdWriter.execShutdown(clientMessage);
		}
		catch (IOException e) {
			log.error("Socket error: {}", e.getMessage());
		}
	}

	private void sendAck(Socket socket) throws IOException {
		writeToSocket(socket, ACK);
	}

	private void writeToSocket(Socket socket, String message) throws IOException {
		DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
		outToClient.writeBytes(message + "\n");
		log.info("Response ACK sent: {}", message);
	}

	private String getIncomingMessage(Socket socket) throws IOException {
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String clientMessage = inFromClient.readLine();
		log.info("Socket message read");
		return clientMessage;
	}

	String getLocalHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e) {
			return "<Unknown>";
		}
	}

}
