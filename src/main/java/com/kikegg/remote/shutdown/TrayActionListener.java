package com.kikegg.remote.shutdown;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public List<String> getIPv4Addresses() {
		Stream<NetworkInterface> networkInterfaceStream;
		try {
			networkInterfaceStream = NetworkInterface.networkInterfaces();
		}
		catch (SocketException e) {
			log.warn(e.getMessage());
			networkInterfaceStream = Stream.empty();
		}

		return networkInterfaceStream.flatMap(NetworkInterface::inetAddresses)
			.filter(inetAddress -> !inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
			.map(InetAddress::getHostAddress)
			.sorted()
			.collect(Collectors.toList());
	}

}
