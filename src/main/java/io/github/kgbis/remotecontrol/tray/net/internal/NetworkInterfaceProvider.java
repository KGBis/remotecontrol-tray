package io.github.kgbis.remotecontrol.tray.net.internal;

import jakarta.inject.Singleton;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

@Singleton
public class NetworkInterfaceProvider {

	public NetworkInterface getByInetAddress(InetAddress inetAddress) throws SocketException {
		return NetworkInterface.getByInetAddress(inetAddress);
	}

}
