package io.github.kgbis.remotecontrol.tray.ui;

import com.google.inject.Inject;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeListener;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionListener;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;

@Singleton
@Slf4j
public class TrayBuilder {

	private final NetworkChangeListener networkChangeListener;

	@Inject
	public TrayBuilder(NetworkChangeListener networkChangeListener) {
		this.networkChangeListener = networkChangeListener;
	}

	@SuppressWarnings("UnusedReturnValue")
	public TrayBuilder loadTray() {
		TrayIcon trayIcon;
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();

			// to listen for default action executed on the tray icon
			ActionListener actionListener = new TrayActionListener(networkChangeListener);

			// create a popup menu
			PopupMenu popup = new PopupMenu();
			popup.setName(REMOTE_PC_CONTROL);

			// create menu item for the default action
			MenuItem defaultItem = new MenuItem("Show Computer IP");
			defaultItem.setActionCommand("IP_CMD");
			defaultItem.addActionListener(actionListener);
			popup.add(defaultItem);

			popup.addSeparator();

			// create menu item for the default action
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.setActionCommand("EXIT_CMD");
			exitItem.addActionListener(actionListener);
			popup.add(exitItem);

			// construct a TrayIcon and set properties
			trayIcon = new TrayIcon(IconImage.getIcon(), REMOTE_PC_CONTROL, popup);
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(actionListener);

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
