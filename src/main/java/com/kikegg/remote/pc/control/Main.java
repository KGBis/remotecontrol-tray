package com.kikegg.remote.pc.control;

import com.kikegg.remote.pc.control.network.NetListener;
import com.kikegg.remote.pc.control.tray.TrayBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Main {

	public static final String REMOTE_PC_CONTROL = "Remote PC Control Tray";

	public static void main(String[] args) {
		try {
			new TrayBuilder().loadTray();
			new NetListener().setTest(args).listen();
		}
		catch (IOException | ExecutionException | InterruptedException e) {
			log.error("Something bad happened. Please report the following error: ", e);
			Thread.currentThread().interrupt();
			System.exit(-1);
		}
	}

}