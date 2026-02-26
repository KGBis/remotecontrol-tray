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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LinuxAutoStartManager implements AutoStartManager {

	private final Path desktopFile = Paths.get(System.getProperty("user.home"), ".config", "autostart",
			"remotecontroltray.desktop");

	private static final String DESKTOP_ENTRY = """
			[Desktop Entry]
			Type=Application
			Name=RemoteControlTray
			Exec=remotecontroltray
			X-GNOME-Autostart-enabled=true
			""";

	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return Files.exists(desktopFile);
	}

	@Override
	public void enable() throws IOException {
		Files.createDirectories(desktopFile.getParent());
		Files.writeString(desktopFile, DESKTOP_ENTRY);
	}

	@Override
	public void disable() throws IOException {
		Files.deleteIfExists(desktopFile);
	}

}
