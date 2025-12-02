package com.kikegg.remote.pc.control.tray;

import com.kikegg.remote.pc.control.network.server.NetworkChangeCallbackImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionListener;

import static com.kikegg.remote.pc.control.Main.REMOTE_PC_CONTROL;

@RequiredArgsConstructor
@Slf4j
public class TrayBuilder {

	private final NetworkChangeCallbackImpl networkChangeCallback;

	@SuppressWarnings("UnusedReturnValue")
	public TrayBuilder loadTray() {
		TrayIcon trayIcon;
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();

			// to listen for default action executed on the tray icon
			ActionListener listener = new TrayActionListener(networkChangeCallback);

			// create a popup menu
			PopupMenu popup = new PopupMenu();
			popup.setName(REMOTE_PC_CONTROL);

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
			trayIcon = new TrayIcon(IconImage.getIcon(), REMOTE_PC_CONTROL, popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(listener);

			// add the tray image
			try {
				tray.add(trayIcon);
			}
			catch (AWTException e) {
				log.warn("Could not add tray icon: {}", e.getMessage());
			}
		}
		else {
			log.error(
					"System tray is not supported. Sorry. Application will be working as expected but without system tray menu.");
		}

		return this;
	}

}
