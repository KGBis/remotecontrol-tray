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
	 * Detect if we're in Gnome. Used to hide "exit" button as its mouse event is consumed
	 * before, hence setting window not visible instead of exiting
	 * @return true if Gnome is Window Manager
	 */
	public static boolean isKde() {
		return desktopEnv().contains("kde");
	}

	/**
	 * Detect if we're in KDE. KDE does return it supports AWT System Tray but even if
	 * show the tray icon, it does not fire the listeners' events, so it's completely
	 * useless.
	 * @return true if KDE is Window Manager
	 */
	public static boolean isGnome() {
		return desktopEnv().contains("gnome");
	}

	public static boolean isFedoraPatchedGnome() {
		return System.getenv("PTYXIS_VERSION") != null;
	}

	// get a lowercase string for desktop enviroment
	private static String desktopEnv() {
		String desktop = System.getenv("XDG_CURRENT_DESKTOP");
		// Fallback to DESKTOP_SESSION
		if (StringUtils.isBlank(desktop)) {
			desktop = System.getenv("DESKTOP_SESSION");
		}

		return StringUtils.isBlank(desktop) ? "" : desktop.toLowerCase();
	}

	/**
	 * Inititalize system tray icon depending on OS support
	 */
	public void initializeTray() {
		EventQueue.invokeLater(() -> {
			if (isKde() || isFedoraPatchedGnome()) {
				log.info("{} detected. No System Tray. It does not support it correctly.", isKde() ? "KDE" : "Fedora");
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
