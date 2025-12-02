package com.kikegg.remote.pc.control;

import com.kikegg.remote.pc.control.network.server.NetworkChangeCallbackImpl;
import com.kikegg.remote.pc.control.network.server.NetworkInfoProvider;
import com.kikegg.remote.pc.control.network.server.NetworkServer;
import com.kikegg.remote.pc.control.tray.TrayBuilder;
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