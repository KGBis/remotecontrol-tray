package com.kikegg.remote.pc.control.network;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public abstract class NetworkAction {

	protected final Socket socket;

	protected final String[] args;

	public abstract void execute() throws IOException;

	void writeToSocket(Socket socket, String message) throws IOException {
		DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
		outToClient.writeBytes(message + "\n");
		outToClient.flush();
		log.info("Response sent: {}", message);
	}

	public static List<String> getIPv4Addresses() {
		Stream<NetworkInterface> networkInterfaceStream;
		try {
			networkInterfaceStream = Collections.list(NetworkInterface.getNetworkInterfaces()).stream();
		}
		catch (SocketException e) {
			log.warn(e.getMessage());
			networkInterfaceStream = Stream.empty();
		}

		return networkInterfaceStream
			.flatMap(networkInterface -> Collections.list(networkInterface.getInetAddresses()).stream())
			.filter(inetAddress -> !inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
			.map(InetAddress::getHostAddress)
			.sorted()
			.collect(Collectors.toList());
	}

}
