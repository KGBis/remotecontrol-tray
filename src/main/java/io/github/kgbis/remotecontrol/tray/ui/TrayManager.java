package io.github.kgbis.remotecontrol.tray.ui;

import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
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

	private final TrayController controller;

	@Inject
	public TrayManager(TrayController controller/* , ResourcesHelper resourcesHelper */) {
		this.controller = controller;
	}

	public void initializeTray() {
		if (!SystemTray.isSupported()) {
			log.error("System tray is not supported. Running without tray menu.");
			return;
		}

		SystemTray tray = SystemTray.getSystemTray();

		TrayIcon trayIcon = new TrayIcon(ResourcesHelper.getIcon(), REMOTE_PC_CONTROL);
		trayIcon.setImageAutoSize(true);
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// single click as toggle
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
