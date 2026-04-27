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

import lombok.Getter;

import java.awt.Color;

public enum HighlightType {

	HOVER(new Color(0, 120, 255, 40), new Color(0, 120, 255, 200)),
	SELECTED(new Color(0, 120, 25, 40), new Color(0, 120, 25, 200)),
	DISABLED(new Color(100, 100, 100, 40), new Color(100, 100, 100, 200));

	@Getter
	private final Color light;

	@Getter
	private final Color dark;

	HighlightType(Color light, Color dark) {
		this.light = light;
		this.dark = dark;
	}

}
