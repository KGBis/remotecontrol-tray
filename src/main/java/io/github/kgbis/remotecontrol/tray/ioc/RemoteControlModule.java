/*
 * Remote PC Control
 * Copyright (C) 2026 Enrique García (https://github.com/KGBis)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.jthemedetecor.OsThemeDetector;
import io.github.kgbis.remotecontrol.tray.autostart.AutoStartController;
import io.github.kgbis.remotecontrol.tray.autostart.AutoStartControllerImpl;
import io.github.kgbis.remotecontrol.tray.bootstrap.BootstrapVersionProvider;
import io.github.kgbis.remotecontrol.tray.bootstrap.BootstrapVersionProviderImpl;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigStorage;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigStorageImpl;
import io.github.kgbis.remotecontrol.tray.net.actions.NetworkActionFactory;
import io.github.kgbis.remotecontrol.tray.net.mdns.JmDNSFactory;
import io.github.kgbis.remotecontrol.tray.net.mdns.JmDNSFactoryImpl;
import io.github.kgbis.remotecontrol.tray.net.server.ServerLoopRunner;
import io.github.kgbis.remotecontrol.tray.net.server.ServerLoopRunnerImpl;
import io.github.kgbis.remotecontrol.tray.net.server.ServerSocketFactory;
import io.github.kgbis.remotecontrol.tray.net.server.ServerSocketFactoryImpl;
import io.github.kgbis.remotecontrol.tray.ui.support.ActionDesktopNotifier;
import io.github.kgbis.remotecontrol.tray.ui.support.DialogHandler;
import io.github.kgbis.remotecontrol.tray.ui.support.DialogHandlerImpl;
import io.github.kgbis.remotecontrol.tray.ui.support.SettingsDialogFactory;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class RemoteControlModule extends AbstractModule {

	/**
	 * {@inheritDoc}
	 * <p>
	 * Only Factories, eager singletons and default interface implementations are
	 * configured here. The rest of {@link Singleton} annotated classes are not set here
	 * for clarity. Guice scans all them all automatically.
	 */
	@Override
	protected void configure() {
		FactoryModuleBuilder factoryModuleBuilder = new FactoryModuleBuilder();
		install(factoryModuleBuilder.build(NetworkActionFactory.class));
		install(factoryModuleBuilder.build(SettingsDialogFactory.class));

		bind(ActionDesktopNotifier.class).asEagerSingleton();
		bind(AutoStartController.class).to(AutoStartControllerImpl.class).in(Singleton.class);
		bind(BootstrapVersionProvider.class).to(BootstrapVersionProviderImpl.class).in(Singleton.class);
		bind(ConfigStorage.class).to(ConfigStorageImpl.class).in(Singleton.class);
		bind(DialogHandler.class).to(DialogHandlerImpl.class).in(Singleton.class);
		bind(JmDNSFactory.class).to(JmDNSFactoryImpl.class).in(Singleton.class);
		bind(ServerLoopRunner.class).to(ServerLoopRunnerImpl.class).in(Singleton.class);
		bind(ServerSocketFactory.class).to(ServerSocketFactoryImpl.class).in(Singleton.class);
	}

	@Provides
	@Singleton
	public OsThemeDetector provideOsThemeDetector() {
		return OsThemeDetector.getDetector();
	}

}
