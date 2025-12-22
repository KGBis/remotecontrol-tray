/*
 * Copyright (c) Enrique García
 *
 * This file is part of RemoteControlTray.
 *
 * RemoteControlTray is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RemoteControlTray is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RemoteControlTray.  If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.ioc;

import com.google.inject.AbstractModule;
import io.github.kgbis.remotecontrol.tray.RemoteControl;
import io.github.kgbis.remotecontrol.tray.net.actions.NetworkActionFactory;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.internal.DeviceIdProvider;
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
		bind(DeviceIdProvider.class).in(Singleton.class);
		bind(InformationScreen.class).in(Singleton.class);
		bind(JmDNSFactory.class).to(JmDNSFactoryDefaultImpl.class).in(Singleton.class);
		bind(NetworkActionFactory.class).in(Singleton.class);
		bind(NetworkInfoProvider.class).in(Singleton.class);
		bind(NetworkInterfaces.class).in(Singleton.class);
		bind(NetworkMulticastManager.class).in(Singleton.class);
		bind(NetworkServer.class).in(Singleton.class);
		bind(RemoteControl.class).in(Singleton.class);
		bind(ServerLoopRunner.class).to(ServerLoopRunnerDefaultImpl.class).in(Singleton.class);
		bind(ServerSocketFactory.class).to(ServerSocketFactoryDefaultImpl.class).in(Singleton.class);
		bind(SystemInfo.class).in(Singleton.class);
		bind(TrayController.class).in(Singleton.class);
		bind(TrayManager.class).in(Singleton.class);
	}

}
