package io.github.kgbis.remotecontrol.tray.ui;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;

@Singleton
@Slf4j
public class TrayManager {

	private final TrayActionListener listener;

	private final TrayController controller;

	private final IconImage iconImage; // Tu proveedor de la imagen

	@Inject
	public TrayManager(TrayActionListener listener, TrayController controller, IconImage iconImage) {
		this.listener = listener;
		this.controller = controller;
		this.iconImage = iconImage;
	}

	public void initializeTray() {
		if (!SystemTray.isSupported()) {
			log.error("System tray is not supported. Running without tray menu.");
			return;
		}

		SystemTray tray = SystemTray.getSystemTray();
		PopupMenu popup = new PopupMenu();

		MenuItem showIp = new MenuItem("Show Computer IP");
		showIp.setActionCommand("IP_CMD");
		showIp.addActionListener(listener);
		popup.add(showIp);

		popup.addSeparator();

		MenuItem exit = new MenuItem("Exit");
		exit.setActionCommand("EXIT_CMD");
		exit.addActionListener(listener);
		popup.add(exit);

		TrayIcon trayIcon = new TrayIcon(iconImage.getIcon(), REMOTE_PC_CONTROL, popup);
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(listener);

		// single clik as toggle
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1 && SwingUtilities.isLeftMouseButton(e)) {
					controller.toggleWindow();
				}
			}
		});

		try {
			tray.add(trayIcon);
		}
		catch (AWTException e) {
			log.warn("Could not add tray icon: {}", e.getMessage());
		}
	}

}
