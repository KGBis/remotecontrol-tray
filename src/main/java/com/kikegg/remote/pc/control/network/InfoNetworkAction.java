package com.kikegg.remote.pc.control.network;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.List;

@Slf4j
public class InfoNetworkAction extends NetworkAction {

	public InfoNetworkAction(Socket socket, String[] args) {
		super(socket, args);
	}

	@Override
	public void execute() throws IOException {
		// check if IP argument is me
		List<String> iPv4Addresses = NetworkAction.getIPv4Addresses();
		if (!iPv4Addresses.contains(args[1])) {
			log.warn("Request contains an unknown IP. {} vs {}", args[1], iPv4Addresses);
			return;
		}

		// "HOSTNAME MAC"
        long s = System.currentTimeMillis();
		String msg = String.format("%s %s", getLocalHostName(), getMac());
        log.info("Took {}ms to get hostname and mac", (System.currentTimeMillis() -s));
		writeToSocket(socket, msg);
	}

	private String getLocalHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e) {
			return args[1]; // requested IP
		}
	}

    private String getMac() {
        InetAddress localHost;
        try {
            localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();
            if (hardwareAddress != null) {
                String[] hexadecimalFormat = new String[hardwareAddress.length];
                for (int i = 0; i < hardwareAddress.length; i++) {
                    hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
                }
                return String.join(":", hexadecimalFormat);
            }
        } catch (Exception e) {
            log.error("error while getting MAC:", e);
        }
        return "";
    }

}
