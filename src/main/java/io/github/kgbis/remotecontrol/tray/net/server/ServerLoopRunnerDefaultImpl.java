package io.github.kgbis.remotecontrol.tray.net.server;

import jakarta.inject.Singleton;

@Singleton
public class ServerLoopRunnerDefaultImpl implements ServerLoopRunner {

	private Thread thread;

	@Override
	public void start(Runnable loop) {
		thread = new Thread(loop, "socket-thread");
		thread.start();
	}

	@Override
	public void stop() throws InterruptedException {
		if (thread != null) {
			thread.join(2000);
		}
	}

}
