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

import io.github.kgbis.remotecontrol.tray.i18n.I18nService;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public record Localized(JComponent component, Supplier<String> value, Consumer<String> applier) {

	public static Localized text(JLabel label, String key, I18nService i18n) {
		return new Localized(label, () -> i18n.get(key), label::setText);
	}

	public static Localized text(AbstractButton button, String key, I18nService i18n) {
		return new Localized(button, () -> i18n.get(key), button::setText);
	}

	public static Localized tooltip(JComponent comp, String key, I18nService i18n) {
		return new Localized(comp, () -> i18n.get(key), comp::setToolTipText);
	}

	public static Localized tableHeaders(JTable table, String keyPrefix, I18nService i18n) {
		return new Localized(table, () -> null, // supplier value not used
				unused -> {
					IntStream.range(0, table.getColumnCount()).forEach(column -> {
						String key = keyPrefix + column;
						table.getColumnModel().getColumn(column).setHeaderValue(i18n.get(key));
					});
					table.getTableHeader().repaint();
				});
	}

	public static Localized tableRepaint(JTable table) {
		return new Localized(table, () -> null, unused -> table.repaint());
	}
}
