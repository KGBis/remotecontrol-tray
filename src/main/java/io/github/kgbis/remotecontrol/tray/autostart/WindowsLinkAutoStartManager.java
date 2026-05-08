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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.APP_NAME;

@Slf4j
public class WindowsLinkAutoStartManager implements AutoStartManager {

	protected final Path startupDir = Paths.get(System.getenv("APPDATA"), "Microsoft", "Windows", "Start Menu",
			"Programs", "Startup");

	protected final Path shortcut = startupDir.resolve("RemoteControlTray.lnk");

	/**
	 * Creates a link from<br>
	 * <i>C:\Users\&lt;user&gt;\AppData\Local\remotecontrol-tray\remotecontrol-tray.exe</i><br>
	 * to user's startup file<br>
	 * <i>C:\Users\&lt;user&gt;\AppData\Roaming\Microsoft\Windows\Start
	 * Menu\Programs\Startup\remotecontrol-tray.lnk</i>
	 * @throws IOException if create shortcut process cannot be run
	 * @throws InterruptedException if create shortcut thread is interrupted while waiting
	 * for the process to end
	 */
	@Override
	public void enable() throws IOException, InterruptedException {
		Path exe = locateInstalledExe();
		WindowsShortcutCreator.createShortcut(shortcut, exe);
	}

	/**
	 * Deletes <i>C:\Users\&lt;user&gt;\AppData\Roaming\Microsoft\Windows\Start
	 * Menu\Programs\Startup\remotecontrol-tray.lnk</i> so on next login program will not
	 * automatically run
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void disable() throws IOException {
		Files.deleteIfExists(shortcut);
	}

	protected Path locateInstalledExe() {
		return Paths.get(System.getenv("LOCALAPPDATA"), APP_NAME, APP_NAME + ".exe");
	}

}