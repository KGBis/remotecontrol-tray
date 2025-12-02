package com.kikegg.remote.pc.control.network.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import oshi.hardware.NetworkIF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class NetworkChangeCallbackImpl implements NetworkChangeCallback {

    private final Map<String, String> ipMacMap = new HashMap<>();

    @Override
    public void onNetworkChange(List<NetworkIF> activeInterfaces) {
        Map<String, String> newMap = new HashMap<>();
        activeInterfaces.forEach(net -> {
            for (String ip : net.getIPv4addr()) {
                newMap.put(ip, net.getMacaddr());
                log.info("Detected {} -> {}", ip, net.getMacaddr());
            }
        });
        ipMacMap.clear();
        ipMacMap.putAll(newMap);
    }
}
