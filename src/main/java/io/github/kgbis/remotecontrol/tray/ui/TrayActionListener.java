package io.github.kgbis.remotecontrol.tray.ui;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeListener;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Slf4j
public class TrayActionListener implements ActionListener {

	private final ShowIpFrame ipFrame;

	TrayActionListener(NetworkChangeListener networkChangeListener) {
		ipFrame = new ShowIpFrame(networkChangeListener);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		MenuItem mi = (MenuItem) source;
		PopupMenu parent = (PopupMenu) mi.getParent();
		parent.setEnabled(false); // Disable the tray temporally

		switch (e.getActionCommand()) {
			case "EXIT_CMD":
				System.exit(0);
				break;
			case "IP_CMD":
				ipFrame.show(parent);
				break;
			default:
				break;
		}
	}

}
