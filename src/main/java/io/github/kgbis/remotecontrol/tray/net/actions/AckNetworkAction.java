package io.github.kgbis.remotecontrol.tray.net.actions;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

@Slf4j
public class AckNetworkAction extends NetworkAction {

	public AckNetworkAction(Socket socket, String[] args, NetworkInfoProvider provider) {
		super(socket, args, provider);
	}

	@Override
	public void execute() throws IOException {
		log.info("ACK sent for args={}", (args != null ? Arrays.toString(args) : ""));
		writeToSocket(socket, "ACK");
	}

	@Override
	protected <T> T parseArguments() {
		return null;
	}

}
