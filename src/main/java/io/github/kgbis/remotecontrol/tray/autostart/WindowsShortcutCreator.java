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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.io.IOException;
import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WindowsShortcutCreator {

	public static void createShortcut(Path shortcut, Path exePath) throws IOException, InterruptedException {

		String ps = """
				$WshShell = New-Object -ComObject WScript.Shell
				$Shortcut = $WshShell.CreateShortcut('%s')
				$Shortcut.TargetPath = '%s'
				$Shortcut.WorkingDirectory = '%s'
				$Shortcut.Save()
				""".formatted(shortcut.toAbsolutePath(), exePath.toAbsolutePath(),
				exePath.getParent().toAbsolutePath());

		Process process = new ProcessBuilder("powershell", "-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", ps)
			.start();

		if (process.waitFor() != 0) {
			throw new IOException("Failed to create shortcut");
		}
	}

}
