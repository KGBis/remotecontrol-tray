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
package io.github.kgbis.remotecontrol.tray.bootstrap;

import io.github.kgbis.remotecontrol.tray.autostart.AutoStartController;
import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.ui.support.DialogHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class Bootstrap {

	private final DialogHandler dialogHandler;

	private final ConfigManager configManager;

	private final AutoStartController autoStartManagerFactory;

	private final BootstrapVersionProvider versionProvider;

	@Inject
	public Bootstrap(DialogHandler dialogHandler, ConfigManager configManager,
			AutoStartController autoStartManagerFactory, BootstrapVersionProvider versionProvider) {
		this.dialogHandler = dialogHandler;
		this.configManager = configManager;
		this.autoStartManagerFactory = autoStartManagerFactory;
		this.versionProvider = versionProvider;
	}

	public void execute() {
		Config config = configManager.current();
		int appVersionLevel = versionProvider.current();

		// check if configuration exists and is up-to-date
		if (!config.isInitialized(appVersionLevel)) {
			config = dialogHandler.run(appVersionLevel);
		}

		// Syncronize auto start on login
		autoStartManagerFactory.syncAutoStart(config.isAppAutoStartOnLogin());
	}

}
