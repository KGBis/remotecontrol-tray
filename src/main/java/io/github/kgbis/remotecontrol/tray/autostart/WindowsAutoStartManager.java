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
package io.github.kgbis.remotecontrol.tray.autostart;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.APP_NAME;

@Slf4j
public class WindowsAutoStartManager implements AutoStartManager {

	private final Path startupDir = Paths.get(System.getenv("APPDATA"), "Microsoft", "Windows", "Start Menu",
			"Programs", "Startup");

	private final Path shortcut = startupDir.resolve("RemoteControlTray.lnk");

	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return Files.exists(shortcut);
	}

	/**
	 * Creates a link from<br>
	 * <i>C:\Users\&lt;user&gt;\AppData\Local\RemoteControlTray\RemoteControlTray.exe</i><br>
	 * to user's startup file<br>
	 * <i>C:\Users\&lt;user&gt;\AppData\Roaming\Microsoft\Windows\Start
	 * Menu\Programs\Startup\RemoteControlTray.lnk</i>
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
	 * Menu\Programs\Startup\RemoteControlTray.lnk</i> so on next login program will not
	 * automatically run
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	public void disable() throws IOException {
		Files.deleteIfExists(shortcut);
	}

	private Path locateInstalledExe() {
		return Paths.get(System.getenv("LOCALAPPDATA"), APP_NAME, APP_NAME + ".exe");
	}

}