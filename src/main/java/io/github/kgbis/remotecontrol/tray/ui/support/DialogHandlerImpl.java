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
package io.github.kgbis.remotecontrol.tray.ui.support;

import io.github.kgbis.remotecontrol.tray.autostart.AutoStartController;
import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.configuration.Settings;
import io.github.kgbis.remotecontrol.tray.ui.SettingsDialog;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.swing.JFrame;

@Slf4j
@Singleton
public class DialogHandlerImpl implements DialogHandler {

	private final ConfigManager configManager;

	private final AutoStartController autoStartController;

	private final SettingsDialogFactory settingsDialogFactory;

	@Inject
	public DialogHandlerImpl(ConfigManager configManager, AutoStartController autoStartController,
			SettingsDialogFactory settingsDialogFactory) {
		this.configManager = configManager;
		this.autoStartController = autoStartController;
		this.settingsDialogFactory = settingsDialogFactory;
	}

	/**
	 * Convenience method that triggers the first-run configuration using default
	 * settings.
	 * @return Settings values
	 */
	@Override
	public Config run(int appVersionLevel) {
		return run(null, DialogMode.ONBOARDING, appVersionLevel);
	}

	@Override
	public Config run(JFrame parent, DialogMode mode) {
		return run(parent, mode, -1);
	}

	private Config run(JFrame parent, DialogMode mode, int versionLevel) {
		SettingsDialog dialog = settingsDialogFactory.create(parent, mode, getConfiguration(), versionLevel);
		dialog.setVisible(true);
		Settings settings = dialog.getSettings();

		// update configuration
		return updateConfig(settings, mode, versionLevel);
	}

	private Config getConfiguration() {
		return configManager.current();
	}

	private Config updateConfig(Settings settings, DialogMode mode, int appVersionLevel) {
		// get current config
		Config config = configManager.current();

		// if no changes, return
		if (settings == null) {
			return config;
		}

		config.setAppAutoStartOnLogin(settings.autoStart());
		config.setLocale(settings.language());

		// If coming from onboarding, save app version level
		if (mode.equals(DialogMode.ONBOARDING)) {
			config.setOnboardingVersion(appVersionLevel);
		}

		// save configuration
		configManager.save(config);

		// Sync autostart
		autoStartController.syncAutoStart(config.isAppAutoStartOnLogin());

		// return config
		return config;
	}

}
