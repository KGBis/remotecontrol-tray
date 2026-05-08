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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * This class extends {@linkplain WindowsLinkAutoStartManager} to swap "automagically"
 * from startup folder to registry's current user run key/value.
 */
@Slf4j
public class WindowsRegistryAutoStartManager extends WindowsLinkAutoStartManager {

	/**
	 * Adds executable to registry's current user run section. <br>
	 * <i>NOTE:</i> This also removes old startup folder .lnk file if exists
	 */
	@Override
	public void enable() {
		removeStartupLink();
		String exePathString = normalizePath();
		WindowsRegistryRunKeyHelper.addEntry(exePathString);
	}

	/**
	 * Removes executable from registry's current user run section
	 */
	@Override
	public void disable() {
		WindowsRegistryRunKeyHelper.removeEntry();
	}

	/*
	 * Add double quotes at beginning and end of the executable path
	 */
	private String normalizePath() {
		return StringUtils.wrap(super.locateInstalledExe().toString(), "\"");
	}

	/*
	 * Create registry entry and delete startup folder link
	 */
	private void removeStartupLink() {
		try {
			super.disable();
		}
		catch (IOException e) {
			log.warn("Cannot delete link in user's startup folder: {}", e.getMessage());
		}
	}

}
