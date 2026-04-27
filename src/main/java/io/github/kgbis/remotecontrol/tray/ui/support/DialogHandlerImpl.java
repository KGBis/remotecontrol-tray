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
package io.github.kgbis.remotecontrol.tray.ui.support;

import io.github.kgbis.remotecontrol.tray.autostart.AutoStartController;
import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.ui.settings.SettingsDialog;
import io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel;
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

	/**
	 * Convenience method when called from the application's settings button.
	 * @param parent Application's UI frame
	 * @param mode currently always {@link DialogMode#SETTINGS}
	 * @return Settings values
	 */
	@Override
	public Config run(JFrame parent, DialogMode mode) {
		return run(parent, mode, 0);
	}

	/*
	 * Create settings dialog and get its values when closed to update configuration
	 */
	private Config run(JFrame parent, DialogMode mode, int versionLevel) {
		SettingsDialog dialog = settingsDialogFactory.create(parent, mode, versionLevel);
		dialog.setVisible(true);
		SettingsModel settingsModel = dialog.getSettingsModel();
		return updateConfig(settingsModel, mode, versionLevel);
	}

	private Config getConfiguration() {
		return configManager.current();
	}

	private Config updateConfig(SettingsModel settingsModel, DialogMode mode, int appVersionLevel) {
		// get current config
		Config config = getConfiguration();

		if (settingsModel == null) {
			return config;
		}

		// apply only if real changes are made (no cancel button or exit clicked)
		settingsModel.applyTo(config);

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
