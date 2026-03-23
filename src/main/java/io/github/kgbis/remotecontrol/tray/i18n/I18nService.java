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
package io.github.kgbis.remotecontrol.tray.i18n;

import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class I18nService {

	private final SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

	@Getter
	private Locale locale;

	private ResourceBundle resourceBundle;

	@Inject
	public I18nService(ConfigManager configManager) {
		locale = configManager.current().getLocale();
		setResourceBundleLocale();
	}

	/**
	 * Set a new locale for i18n. Every time this method is called, it fires a property
	 * change event -only if locale changes-.
	 * @param newLocale the new locale to use
	 */
	public void setLocale(Locale newLocale) {
		Locale old = locale;

		// Same local does not trigger any change
		if (Objects.equals(old, newLocale)) {
			return;
		}

		locale = newLocale;
		setResourceBundleLocale();
		pcs.firePropertyChange("locale", old, newLocale);
	}

	public String get(String key) {
		String value = resourceBundle.getString(key);
		return resolve(value);
	}

	/**
	 * register a {@linkplain PropertyChangeListener}
	 * @param listener listener to register
	 */
	public void addListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Unregister a {@linkplain PropertyChangeListener} <br>
	 * Not needed right now as there are no disposable windows registered
	 * @param listener Property change listener
	 */
	@SuppressWarnings("unused")
	public void removeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	private void setResourceBundleLocale() {
		resourceBundle = ResourceBundle.getBundle("locales/locale", locale);
	}

	/**
	 * Resolve {@code ${placeholder}} inside i18n property values
	 * @param value source i18n value
	 * @return resolved string
	 */
	private String resolve(String value) {
		Pattern pattern = Pattern.compile("\\$\\{(.+?)}");
		Matcher matcher = pattern.matcher(value);

		StringBuilder result = new StringBuilder();
		while (matcher.find()) {
			String subKey = matcher.group(1);
			String replacement = resourceBundle.getString(subKey);
			matcher.appendReplacement(result, replacement);
		}
		matcher.appendTail(result);

		return result.toString();
	}

}
