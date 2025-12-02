package com.kikegg.remote.pc.control.network.server;

import oshi.hardware.NetworkIF;

import java.util.List;

public interface NetworkChangeCallback {
    void onNetworkChange(List<NetworkIF> activeInterfaces);
}
