package io.github.kgbis.remotecontrol.tray.ui;

import dorkbox.systemTray.SystemTray;
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
	public TrayManager(TrayController controller) {
		this.controller = controller;
	}

	/**
	 * Inititalize system tray icon depending on OS support. Why? Because in Windows 11
	 * using dorkbox, the mouse X and Y coordinates returned by the OS are not the real X
	 * and Y coordinates, but {@link Integer#MAX_VALUE} and dorkbox cannot crashes on
	 * click.
	 */
	public void initializeTray() {
		EventQueue.invokeLater(() -> {
			if (java.awt.SystemTray.isSupported()) {
				log.debug("AWT SystemTray supported. Using native implementation.");
				useAwtSystemTray();
			}
			else {
				log.debug("AWT SystemTray NOT supported. Trying to use dorkbox/SystemTray as fallback.");
				useDorkboxSystemTray();
			}
		});
	}

	/**
	 * Supported by Windows and XFCE
	 */
	private void useAwtSystemTray() {
		final java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();

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

	/**
	 * For Gnome.
	 */
	private void useDorkboxSystemTray() {
		final SystemTray systemTray = SystemTray.get();
		if (systemTray == null) {
			log.warn("Unable to use AWT or Dorkbox's tray. Application will continue to run without tray icon.");
			return;
		}

		systemTray.setImage(ResourcesHelper.getIcon());
		systemTray.setStatus(REMOTE_PC_CONTROL).setCallback(e -> controller.toggleWindow());
	}

}
