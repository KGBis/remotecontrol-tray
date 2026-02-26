package io.github.kgbis.remotecontrol.tray.autostart;

import java.io.IOException;

public interface AutoStartManager {

	boolean isSupported();

	boolean isEnabled();

	void enable() throws IOException, InterruptedException;

	void disable() throws IOException;

}
