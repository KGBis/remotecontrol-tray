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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.APP_NAME;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourcesHelper {

	private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

	private static final String USER_HOME = System.getProperty("user.home");

	/* Config and log paths */

	// Windows
	private static final String WIN_LOG_FOLDER = "LOCALAPPDATA";

	private static final String WIN_CONF_FOLDER = "APPDATA";

	// Linux
	private static final String LINUX_LOG_FOLDER = ".cache";

	private static final String LINUX_CONF_FOLDER = ".config";

	// macOS
	private static final String MACOS_LIB_FOLDER = "Library";

	private static final String MACOS_LOG_FOLDER = "Logs";

	private static final String MACOS_CONF_FOLDER = "Application Support";

	private static final String CONFIG_FILE = "device.id";

	private static volatile UUID systemId;

	/* application resources */

	private static Image image;

	private static String version;

	public static Image getIcon() {
		if (image == null) {
			URL resource = ResourcesHelper.class.getClassLoader().getResource("computer.png");
			image = resource != null ? Toolkit.getDefaultToolkit().getImage(resource) : createFallbackIcon();
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

	public static Path getOSLogDirectory() {
		Path logDir;
		if (OS_NAME.contains("win")) {
			String logFolder = System.getenv(WIN_LOG_FOLDER);
			logDir = logFolder != null ? Path.of(logFolder, APP_NAME, "logs") : Path.of(USER_HOME, APP_NAME, "logs");
		}
		else if (OS_NAME.contains("mac")) {
			logDir = Path.of(USER_HOME, MACOS_LIB_FOLDER, MACOS_LOG_FOLDER, APP_NAME);
		}
		else {
			// Linux/Unix
			logDir = Path.of(USER_HOME, LINUX_LOG_FOLDER, APP_NAME, "logs");
		}

		createDir(logDir);
		return logDir;
	}

	public static synchronized UUID getSystemId() throws IOException {
		if (systemId != null) {
			return systemId;
		}

		UUID uuid;

		Path osConfigFile = getOSConfigDirectory().resolve(CONFIG_FILE);
		if (Files.exists(osConfigFile)) {
			uuid = UUID.fromString(Files.readString(osConfigFile).trim());
		}
		else {
			uuid = UUID.randomUUID();
			Files.writeString(osConfigFile, uuid.toString(), CREATE_NEW);
		}

		systemId = uuid;
		return uuid;
	}

	public static Path getOSConfigDirectory() {
		Path configDir;
		if (OS_NAME.contains("win")) {
			String appData = System.getenv(WIN_CONF_FOLDER);
			configDir = appData != null ? Path.of(appData, APP_NAME) : Path.of(USER_HOME, APP_NAME);

		}
		else if (OS_NAME.contains("mac")) {
			configDir = Path.of(USER_HOME, MACOS_LIB_FOLDER, MACOS_CONF_FOLDER, APP_NAME);
		}
		else {
			// Linux/Unix
			configDir = Path.of(USER_HOME, LINUX_CONF_FOLDER, APP_NAME);
		}

		createDir(configDir);
		return configDir;
	}

	private static void createDir(Path path) {
		try {
			Files.createDirectories(path);
		}
		catch (IOException e) {
			throw new UncheckedIOException("Cannot create directory " + path, e);
		}
	}

	private static Image createFallbackIcon() {
		BufferedImage img = new BufferedImage(32, 22, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();

		try {
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g2d.setColor(new Color(0x2E7D32)); // dark green
			g2d.fillRect(0, 0, 32, 22);

			g2d.setColor(Color.WHITE);
			FontMetrics fm = g2d.getFontMetrics();
			String text = "RC";
			int x = (32 - fm.stringWidth(text)) / 2;
			int y = (22 + fm.getAscent()) / 2 - 2;
			g2d.drawString(text, x, y);
		}
		finally {
			g2d.dispose();
		}

		return img;
	}

}
