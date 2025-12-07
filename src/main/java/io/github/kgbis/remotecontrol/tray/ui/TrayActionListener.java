package io.github.kgbis.remotecontrol.tray.ui;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Singleton
@Slf4j
public class TrayActionListener implements ActionListener {

	private final TrayController controller;

	@Inject
	public TrayActionListener(TrayController controller) {
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		// Doble clic del icono (TrayIcon)
		if (source instanceof TrayIcon) {
			controller.toggleWindow();
			return;
		}

		// Evento de menú (MenuItem)
		if (!(source instanceof MenuItem)) {
			log.warn("Unknown event source {}", source);
			return;
		}

		MenuItem mi = (MenuItem) source;
		PopupMenu parent = (PopupMenu) mi.getParent();
		parent.setEnabled(false);

		switch (e.getActionCommand()) {
			case "EXIT_CMD":
				controller.exitApplication();
				break;
			case "IP_CMD":
				controller.showIpWindow();
				break;
			default:
				log.warn("Unknown action command {}", e.getActionCommand());
		}
	}

}
