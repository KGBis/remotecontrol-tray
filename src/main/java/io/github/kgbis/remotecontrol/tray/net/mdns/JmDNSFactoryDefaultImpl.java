package io.github.kgbis.remotecontrol.tray.net.mdns;

import jakarta.inject.Singleton;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;

@Singleton
public class JmDNSFactoryDefaultImpl implements JmDNSFactory {

    @Override
    public JmDNS create(InetAddress addr) throws IOException {
        return JmDNS.create(addr);
    }
}
