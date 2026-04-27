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
package io.github.kgbis.remotecontrol.tray.autostart;

import jakarta.inject.Singleton;
import org.apache.commons.lang3.SystemProperties;
import org.apache.commons.lang3.SystemUtils;

@Singleton
public class AutoStartControllerImpl implements AutoStartController {

	private static final String UNSUPPORTED = "Unsupported OS: %s";

	private AutoStartService service;

	@Override
	public void syncAutoStart(boolean enabled) {
		ensureInitialized();
		service.syncAutoStart(enabled);
	}

	private void ensureInitialized() {
		if (service != null) {
			return;
		}

		AutoStartManager manager;

		if (SystemUtils.IS_OS_WINDOWS) {
			manager = new WindowsAutoStartManager();
		}
		else if (SystemUtils.IS_OS_LINUX) {
			manager = new LinuxAutoStartManager();
		}
		else if (SystemUtils.IS_OS_MAC) {
			manager = new MacOSAutoStartManager();
		}
		else {
			throw new UnsupportedOperationException(String.format(UNSUPPORTED, SystemProperties.getOsName()));
		}

		service = new AutoStartService(manager);
	}

}