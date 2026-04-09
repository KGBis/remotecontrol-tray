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

import dorkbox.notify.Position;
import io.github.kgbis.remotecontrol.tray.autostart.AutoStartController;
import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.ui.settings.SettingsDialog;
import io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DialogHandlerTest {

	@Mock
	ConfigManager configManager;

	@Mock
	SettingsDialogFactory dialogFactory;

	@Mock
	AutoStartController autoStartController;

	@InjectMocks
	DialogHandlerImpl dialogHandler;

	@Test
	void should_save_config_when_user_accepts_settings() {
		ArgumentCaptor<Config> captor = ArgumentCaptor.forClass(Config.class);

		Config config = Config.builder()
			.onboardingVersion(0)
			.appAutoStartOnLogin(true)
			.locale(Locale.of("es"))
			.showNotifications(true)
			.notificationDuration(NotificationDuration.SHORT.getTtl())
			.notificationPosition(Position.BOTTOM_RIGHT)
			.build();

		SettingsDialog dialog = mock(SettingsDialog.class);
		when(dialog.getSettingsModel()).thenReturn(SettingsModel.of(config));
		doNothing().when(dialog).setVisible(true);

		when(configManager.current()).thenReturn(new Config());
		when(dialogFactory.create(/* any(), */ any(), any(), anyInt())).thenReturn(dialog);
		doNothing().when(autoStartController).syncAutoStart(anyBoolean());

		dialogHandler.run(2);

		verify(configManager).save(captor.capture());

		Config saved = captor.getValue();
		assertTrue(saved.isAppAutoStartOnLogin());
		assertEquals(Locale.of("es"), saved.getLocale());
		assertEquals(2, saved.getOnboardingVersion());
	}

	@Test
	void should_not_save_config_when_user_cancels_settings() {
		SettingsDialog dialog = mock(SettingsDialog.class);
		when(dialog.getSettingsModel()).thenReturn(null);
		doNothing().when(dialog).setVisible(true);

		when(configManager.current()).thenReturn(new Config());
		when(dialogFactory.create(/* any(), */ any(), any(), anyInt())).thenReturn(dialog);

		dialogHandler.run(1);

		verify(configManager, never()).save(any(Config.class));
	}

}
