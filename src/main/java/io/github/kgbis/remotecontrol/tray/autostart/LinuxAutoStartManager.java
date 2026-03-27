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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LinuxAutoStartManager implements AutoStartManager {

	private static final String DESKTOP_ENTRY = """
			[Desktop Entry]
			Name=Remote Control Tray
			Comment=Remote shutdown control tray
			Exec=/opt/remotecontrol-tray/bin/remotecontrol-tray
			Icon=remotecontrol-tray
			Terminal=false
			Type=Application
			Categories=Utility;Network;
			MimeType=
			StartupNotify=true
			Hidden=false
			NoDisplay=false
			X-GNOME-Autostart-enabled=true
			X-KDE-autostart-after=panel
			""";

	private final Path desktopFile = Paths.get(System.getProperty("user.home"), ".config", "autostart",
			"remotecontrol-tray.desktop");

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
