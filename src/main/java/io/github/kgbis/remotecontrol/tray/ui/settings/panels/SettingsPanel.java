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
package io.github.kgbis.remotecontrol.tray.ui.settings.panels;

import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Component;

public abstract class SettingsPanel extends JPanel {

	SettingsPanel(SettingsModel model, I18nService i18nService, boolean markAsNew) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);

		buildPanel(model, i18nService, markAsNew);
	}

	abstract void buildPanel(SettingsModel model, I18nService i18nService, boolean markAsNew);

}
