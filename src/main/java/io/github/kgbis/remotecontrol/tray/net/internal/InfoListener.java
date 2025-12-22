package io.github.kgbis.remotecontrol.tray.net.internal;

import java.util.Map;

public interface InfoListener<K, V> {

    void onChange(Map<K, V> map);
}
