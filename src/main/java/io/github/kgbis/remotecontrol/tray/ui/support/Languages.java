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

import java.util.Arrays;
import java.util.Locale;

public enum Languages {

	ENGLISH("English", Locale.of("en")), SPANISH("Español", Locale.of("es"));

	@Getter
	private final Locale locale;

	@Getter
	private final String text;

	Languages(String text, Locale locale) {
		this.text = text;
		this.locale = locale;
	}

	/**
	 * Returns the enum constant matching the locale provided. It fallbacks to English if
	 * passed locale is null or if not found.<br>
	 * <b>Important:</b> Matches only by language, ignoring country/variant.
	 * @param locale Locale to search for
	 * @return the enum value
	 */
	public static Languages fromLocale(Locale locale) {
		if (locale == null)
			return Languages.ENGLISH;

		return Arrays.stream(Languages.values())
			.filter(l -> l.getLocale().getLanguage().equals(locale.getLanguage()))
			.findFirst()
			.orElse(Languages.ENGLISH);
	}

}
