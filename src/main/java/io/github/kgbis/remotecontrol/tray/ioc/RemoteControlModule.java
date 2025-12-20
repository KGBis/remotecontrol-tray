/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.ioc;

import com.google.inject.AbstractModule;
import io.github.kgbis.remotecontrol.tray.RemoteControl;
import io.github.kgbis.remotecontrol.tray.net.actions.NetworkActionFactory;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.internal.NetworkInterfaces;
import io.github.kgbis.remotecontrol.tray.net.mdns.JmDNSFactory;
import io.github.kgbis.remotecontrol.tray.net.mdns.JmDNSFactoryDefaultImpl;
import io.github.kgbis.remotecontrol.tray.net.mdns.NetworkMulticastManager;
import io.github.kgbis.remotecontrol.tray.net.server.NetworkServer;
import io.github.kgbis.remotecontrol.tray.net.server.ServerLoopRunner;
import io.github.kgbis.remotecontrol.tray.net.server.ServerLoopRunnerDefaultImpl;
import io.github.kgbis.remotecontrol.tray.net.server.ServerSocketFactory;
import io.github.kgbis.remotecontrol.tray.net.server.ServerSocketFactoryDefaultImpl;
import io.github.kgbis.remotecontrol.tray.ui.InformationScreen;
import io.github.kgbis.remotecontrol.tray.ui.TrayController;
import io.github.kgbis.remotecontrol.tray.ui.TrayManager;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;

@Slf4j
public class RemoteControlModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ServerLoopRunner.class).to(ServerLoopRunnerDefaultImpl.class).in(Singleton.class);
		bind(ServerSocketFactory.class).to(ServerSocketFactoryDefaultImpl.class).in(Singleton.class);
		bind(InformationScreen.class).in(Singleton.class);
		bind(JmDNSFactory.class).to(JmDNSFactoryDefaultImpl.class).in(Singleton.class);
		bind(NetworkActionFactory.class).in(Singleton.class);
		bind(NetworkInfoProvider.class).in(Singleton.class);
		bind(NetworkInterfaces.class).in(Singleton.class);
		bind(NetworkMulticastManager.class).in(Singleton.class);
		bind(NetworkServer.class).in(Singleton.class);
		bind(RemoteControl.class).in(Singleton.class);
		bind(SystemInfo.class).in(Singleton.class);
		bind(TrayController.class).in(Singleton.class);
		bind(TrayManager.class).in(Singleton.class);
	}

}
