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
package io.github.kgbis.remotecontrol.tray.misc;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourcesHelper {

	private static Image image;

	private static String version;

	public static Image getIcon() {
		if (image == null) {
			image = Toolkit.getDefaultToolkit()
				.getImage(ResourcesHelper.class.getClassLoader().getResource("computer.png"));
		}
		return image;
	}

	public static String getVersion() {
		if (version == null) {
			try (InputStream in = ResourcesHelper.class.getClassLoader().getResourceAsStream("version.txt")) {
				if (in == null) {
					version = "unknown";
				}
				else {
					version = new String(in.readAllBytes()).trim();
				}
			}
			catch (IOException e) {
				version = "unknown";
			}
		}

		return version;
	}

}
