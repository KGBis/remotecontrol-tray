package com.kikegg.remote.pc.control.network;

import java.io.IOException;
import java.net.Socket;

public class AckNetworkAction extends NetworkAction {

    public AckNetworkAction(Socket socket, String[] args) {
        super(socket, args);
    }

    @Override
    public void execute() throws IOException {
        writeToSocket(socket, "ACK");
    }
}
