package com.kikegg.remote.pc.control.network.actions;

import com.kikegg.remote.pc.control.network.server.NetworkInfoProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

@Slf4j
public class InfoNetworkAction extends NetworkAction {

	public InfoNetworkAction(Socket socket, String[] args, NetworkInfoProvider provider) {
        super(socket, args, provider);
    }

    @Override
    public void execute() throws IOException {
        String ip = parseArguments();

        if (!networkInfoProvider.getIPv4Addresses().contains(ip)) {
            log.warn("Unknown IP requested: {}", ip);
            return;
        }

        String msg = networkInfoProvider.getHostName(ip) + " " + networkInfoProvider.getMac(ip);
        log.info("Responding with: {}", msg);

        OutputStream out = socket.getOutputStream();
        out.write((msg + "\n").getBytes());
        out.flush();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String parseArguments() {
        if (args.length < 2) return null;
        return args[1];
    }

}
