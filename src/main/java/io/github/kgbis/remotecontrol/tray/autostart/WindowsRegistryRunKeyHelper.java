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

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.APP_NAME;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WindowsRegistryRunKeyHelper {

	private static final String RUN_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";

	public static void addEntry(String executablePath) {
		try {
			Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, RUN_KEY, APP_NAME, executablePath);
		}
		catch (Win32Exception e) {
			log.warn("Error adding entry in Windows Registry. Error code: {}", e.getErrorCode());
		}
	}

	public static void removeEntry() {
		try {
			if (isPresent()) {
				Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, RUN_KEY, APP_NAME);
			}
		}
		catch (Win32Exception e) {
			log.warn("Error removing entry in Windows Registry. Error code: {}", e.getErrorCode());
		}
	}

	private static boolean isPresent() {
		try {
			return Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, RUN_KEY, APP_NAME);
		}
		catch (Win32Exception e) {
			log.warn("Error querying Windows Registry. Error code: {}", e.getErrorCode());
			return false;
		}
	}

}
