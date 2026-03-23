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
package io.github.kgbis.remotecontrol.tray.bootstrap;

import io.github.kgbis.remotecontrol.tray.autostart.AutoStartController;
import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.ui.support.DialogHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootstrapTest {

	@Mock
	ConfigManager configManager;

	@Mock
	AutoStartController autoStartController;

	@Mock
	DialogHandler dialogHandler;

	@Mock
	BootstrapVersionProvider versionProvider;

	@Mock
	I18nService i18nService;

	@InjectMocks
	Bootstrap bootstrap;

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void no_config_firstRun_accepts_autostart(boolean value) {
		Config config = new Config();

		when(configManager.current()).thenReturn(config);
		when(versionProvider.current()).thenReturn(1);
		when(i18nService.getLocale()).thenReturn(Locale.ENGLISH);
		when(dialogHandler.run(1)).thenReturn(Config.builder().appAutoStartOnLogin(value).onboardingVersion(1).build());

		bootstrap.execute();

		verify(dialogHandler).run(1);
		verify(autoStartController).syncAutoStart(value);
	}

	@Test
	void upToDate_config_does_not_run_autostart() {
		Config config = Config.builder().onboardingVersion(0).build();

		when(configManager.current()).thenReturn(config);
		when(versionProvider.current()).thenReturn(0);

		bootstrap.execute();

		verify(dialogHandler, never()).run(0);
		verify(configManager, never()).save(any());
		verify(autoStartController).syncAutoStart(config.isAppAutoStartOnLogin());
	}

	@Test
	void version_change_runs_onBoarding() {
		Config config = Config.builder().onboardingVersion(0).build();
		int version = 2;

		when(configManager.current()).thenReturn(config);
		when(versionProvider.current()).thenReturn(version);
		when(dialogHandler.run(version)).thenReturn(
				Config.builder().appAutoStartOnLogin(true).onboardingVersion(version).locale(Locale.ENGLISH).build());

		bootstrap.execute();

		verify(dialogHandler).run(version);
	}

}
