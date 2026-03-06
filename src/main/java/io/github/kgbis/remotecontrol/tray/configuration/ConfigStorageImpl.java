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
package io.github.kgbis.remotecontrol.tray.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@NoArgsConstructor
@Singleton
public class ConfigStorageImpl implements ConfigStorage {

	private static final ObjectMapper mapper = new ObjectMapper();

	private static final Path configFile = ResourcesHelper.getConfigFile();

	@Override
	public boolean exists() {
		return Files.exists(configFile);
	}

	@Override
	public Config read() throws IOException {
		return mapper.readValue(configFile.toFile(), Config.class);
	}

	@Override
	public void write(Config config) throws IOException {
		mapper.writerWithDefaultPrettyPrinter().writeValue(configFile.toFile(), config);
	}

}
