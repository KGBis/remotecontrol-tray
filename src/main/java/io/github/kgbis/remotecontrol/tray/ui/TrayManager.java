/*
 * Copyright (c) Enrique García
 *
 * This file is part of RemoteControlTray.
 *
 * RemoteControlTray is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RemoteControlTray is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RemoteControlTray.  If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.ui;

import dorkbox.systemTray.Menu;
import dorkbox.systemTray.SystemTray;
import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.SwingUtilities;
import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;
import static io.github.kgbis.remotecontrol.tray.ui.support.TraySupportDetector.getDesktop;
import static io.github.kgbis.remotecontrol.tray.ui.support.TraySupportDetector.isNoneTraySupport;

@Singleton
@Slf4j
public class TrayManager {

	static {
		SystemTray.DEBUG = true;
	}

	private final TrayController controller;

	@Inject
	public TrayManager(TrayController controller) {
		this.controller = controller;
	}

	/**
	 * Inititalize system tray icon depending on OS support
	 */
	public void initializeTray() {
		EventQueue.invokeLater(() -> {
			if (isNoneTraySupport()) {
				log.info("{} detected. No System Tray. It does not support it correctly.",
						StringUtils.capitalize(getDesktop()));
				controller.toggleWindow();
				return;
			}

			if (java.awt.SystemTray.isSupported()) {
				log.info("AWT SystemTray supported. Using native implementation.");
				useAwtSystemTray();
			}
			else {
				log.info("AWT SystemTray NOT supported. Trying to use dorkbox/SystemTray as fallback.");
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
		Menu menu;
		try {
			menu = systemTray.setStatus(REMOTE_PC_CONTROL);
		}
		catch (Exception e) {
			log.warn("Error in Dorkbox System Tray: {}", e.getMessage());
			menu = systemTray.setTooltip(REMOTE_PC_CONTROL);
		}
		menu.setCallback(e -> controller.toggleWindow());
	}

}
