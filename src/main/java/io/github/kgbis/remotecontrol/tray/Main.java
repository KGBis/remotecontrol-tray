package io.github.kgbis.remotecontrol.tray;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeCallbackImpl;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.server.NetworkServer;
import io.github.kgbis.remotecontrol.tray.ui.TrayBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {

	public static final String REMOTE_PC_CONTROL = "Remote PC Control Tray";

	public static void main(String[] args) {
		try {
			NetworkChangeCallbackImpl networkChangeCallback = new NetworkChangeCallbackImpl();
			new TrayBuilder(networkChangeCallback).loadTray();
			new NetworkServer(6800, new NetworkInfoProvider(networkChangeCallback)).setTest(args).start();
		}
		catch (IOException e) {
			log.error("Something bad happened. Please report the following error: ", e);
			Thread.currentThread().interrupt();
			System.exit(-1);
		}
	}

}