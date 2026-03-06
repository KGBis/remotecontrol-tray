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
package io.github.kgbis.remotecontrol.tray.bootstrap;

import io.github.kgbis.remotecontrol.tray.autostart.AutoStartController;
import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.ui.support.FirstRunDialogHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;

@Singleton
public class Bootstrap {

	private final FirstRunDialogHandler dialogHandler;

	private final ConfigManager configManager;

	private final AutoStartController autoStartManagerFactory;

	private final BootstrapVersionProvider versionProvider;

	@Inject
	public Bootstrap(FirstRunDialogHandler dialogHandler, ConfigManager configManager,
			AutoStartController autoStartManagerFactory, BootstrapVersionProvider versionProvider) {
		this.dialogHandler = dialogHandler;
		this.configManager = configManager;
		this.autoStartManagerFactory = autoStartManagerFactory;
		this.versionProvider = versionProvider;
	}

	public void execute() throws IOException {
		Config config = configManager.current();
		int bootstrapVersion = versionProvider.current();

		// check if configuration exists and is up-to-date
		if (!config.isInitialized(bootstrapVersion)) {
			BootstrapAutoStart result = dialogHandler.run();
			config.setAppAutoStartOnLogin(result.autoStart());
			config.setOnboardingVersion(bootstrapVersion);
			configManager.save(config);

		}

		// Syncronize auto start on login
		autoStartManagerFactory.syncAutoStart(config.isAppAutoStartOnLogin());

	}

}
