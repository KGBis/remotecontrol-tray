package io.github.kgbis.remotecontrol.tray.net.info;

import oshi.hardware.NetworkIF;

import java.util.List;

public interface NetworkChangeCallback {

	void onNetworkChange(List<NetworkIF> activeInterfaces);

}
