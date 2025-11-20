package com.kikegg.remote.pc.control.tray;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.kikegg.remote.pc.control.network.NetworkAction.getIPv4Addresses;

@Slf4j
public class TrayActionListener implements ActionListener {

	private final ShowIpFrame ipFrame;

	TrayActionListener() {
		ipFrame = new ShowIpFrame();
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
				ipFrame.show(getIPv4Addresses(), parent);
				break;
			default:
				break;
		}
	}

}
