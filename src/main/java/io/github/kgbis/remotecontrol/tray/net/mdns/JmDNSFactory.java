package io.github.kgbis.remotecontrol.tray.net.mdns;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;

public interface JmDNSFactory {

    JmDNS create(InetAddress addr) throws IOException;

}
