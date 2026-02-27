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

public class MacOSAutoStartManager implements AutoStartManager {

	private final Path plist = Paths.get(System.getProperty("user.home"), "Library", "LaunchAgents",
			"io.github.kgbis.remotecontrol.tray.plist");

	@Override
	public boolean isSupported() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return Files.exists(plist);
	}

	@Override
	public void enable() throws IOException {
		Files.createDirectories(plist.getParent());
		Files.writeString(plist, plistContent());
	}

	@Override
	public void disable() throws IOException {
		Files.deleteIfExists(plist);
	}

	private String plistContent() {
		return """
				<?xml version="1.0" encoding="UTF-8"?>
				<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN"
				    "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
				<plist version="1.0">
				<dict>
				    <key>Label</key>
				    <string>io.github.kgbis.remotecontrol.tray</string>

				    <key>ProgramArguments</key>
				    <array>
				        <string>%s</string>
				    </array>

				    <key>RunAtLoad</key>
				    <true/>
				</dict>
				</plist>
				""".formatted(locateExecutable());
	}

	private String locateExecutable() {
		return Paths.get(System.getProperty("user.dir"), "RemoteControlTray").toString();
	}

}