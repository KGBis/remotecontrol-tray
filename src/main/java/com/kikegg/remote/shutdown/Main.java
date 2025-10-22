package com.kikegg.remote.shutdown;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Main {

	public static final String REMOTE_SHUTDOWN = "Remote Shutdown";

	private void loadTray() {
		TrayIcon trayIcon;
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();

			// create a action listener to listen for default action executed on the tray
			// icon
			ActionListener listener = new TrayActionListener();

			// create a popup menu
			PopupMenu popup = new PopupMenu();
			popup.setName(REMOTE_SHUTDOWN);

			// create menu item for the default action
			MenuItem defaultItem = new MenuItem("Show Computer IP");
			defaultItem.setActionCommand("IP_CMD");
			defaultItem.addActionListener(listener);
			popup.add(defaultItem);

			popup.addSeparator();

			// create menu item for the default action
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.setActionCommand("EXIT_CMD");
			exitItem.addActionListener(listener);
			popup.add(exitItem);

			// construct a TrayIcon and set properties
			trayIcon = new TrayIcon(IconImage.getIcon(), REMOTE_SHUTDOWN, popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(listener);

			// add the tray image
			try {
				tray.add(trayIcon);
			}
			catch (AWTException e) {
				log.warn("Could not add tray icon: {}", e.getMessage());
			}
			// ...
		}
		else {
			log.error(
					"System tray is not supported. Sorry. Application will be working as expected but without system tray menu.");
		}
	}

	private void loadServer() throws IOException, ExecutionException, InterruptedException {
		NetListener netListener = new NetListener();
		netListener.listen();
	}

	public static void main(String[] args) {
		Main main = new Main();
		main.loadTray();
		try {
			main.loadServer();
		}
		catch (IOException | ExecutionException | InterruptedException e) {
			log.error("Something happened. Error: ", e);
			Thread.currentThread().interrupt();
			System.exit(-1);
		}
	}

}