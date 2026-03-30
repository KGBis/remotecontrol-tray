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

import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.ui.SettingsDialog;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.swing.JFrame;

@Singleton
public class SettingsDialogFactoryImpl implements SettingsDialogFactory {

	private final I18nService i18nService;

	@Inject
	public SettingsDialogFactoryImpl(I18nService i18nService) {
		this.i18nService = i18nService;
	}

	@Override
	public SettingsDialog create(JFrame parent, DialogMode mode, Config config, int versionLevel) {
		return new SettingsDialog(parent, mode, config, versionLevel, i18nService);
	}

}
