package io.github.kgbis.remotecontrol.tray.net.server;

public interface ServerLoopRunner {

	void start(Runnable loop);

	void stop() throws InterruptedException;

}
