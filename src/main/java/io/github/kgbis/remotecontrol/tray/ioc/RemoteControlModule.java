package io.github.kgbis.remotecontrol.tray.ioc;

import com.google.inject.AbstractModule;
import io.github.kgbis.remotecontrol.tray.RemoteControl;
import io.github.kgbis.remotecontrol.tray.net.actions.NetworkActionFactory;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeListener;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.server.DefaultServerLoopRunner;
import io.github.kgbis.remotecontrol.tray.net.server.DefaultServerSocketFactory;
import io.github.kgbis.remotecontrol.tray.net.server.NetworkServer;
import io.github.kgbis.remotecontrol.tray.net.server.ServerLoopRunner;
import io.github.kgbis.remotecontrol.tray.net.server.ServerSocketFactory;
import io.github.kgbis.remotecontrol.tray.ui.InformationScreen;
import io.github.kgbis.remotecontrol.tray.ui.TrayController;
import io.github.kgbis.remotecontrol.tray.ui.TrayManager;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteControlModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ServerLoopRunner.class).to(DefaultServerLoopRunner.class).in(Singleton.class);
		bind(ServerSocketFactory.class).to(DefaultServerSocketFactory.class).in(Singleton.class);
		bind(InformationScreen.class).in(Singleton.class);
		bind(NetworkActionFactory.class).in(Singleton.class);
		bind(NetworkChangeListener.class).in(Singleton.class);
		bind(NetworkInfoProvider.class).in(Singleton.class);
		bind(NetworkServer.class).in(Singleton.class);
		bind(RemoteControl.class).in(Singleton.class);
		bind(TrayController.class).in(Singleton.class);
		bind(TrayManager.class).in(Singleton.class);
	}

}
