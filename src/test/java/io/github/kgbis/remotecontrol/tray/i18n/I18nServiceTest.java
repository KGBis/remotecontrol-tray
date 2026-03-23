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

import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class I18nServiceTest {

	@Mock
	private ConfigManager configManager;

	@Test
	void when_locale_is_changed_event_is_fired() {
		AtomicReference<PropertyChangeEvent> ref = new AtomicReference<>();

		PropertyChangeListener listener = ref::set;

		when(configManager.current()).thenReturn(Config.builder().build());

		I18nService i18nService = new I18nService(configManager);
		i18nService.addListener(listener);
		i18nService.setLocale(Locale.of("es"));

		PropertyChangeEvent event = ref.get();

		assertNotNull(event);
		assertEquals("locale", event.getPropertyName());
		assertEquals(Locale.of("es"), event.getNewValue());
	}

	@Test
	void when_locale_is_same_event_is_not_fired() {
		AtomicBoolean called = new AtomicBoolean(false);

		when(configManager.current()).thenReturn(Config.builder().build());

		I18nService i18nService = new I18nService(configManager);
		i18nService.addListener(event -> called.set(true));
		i18nService.setLocale(Locale.ENGLISH);

		assertFalse(called.get());
	}

}
