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
package io.github.kgbis.remotecontrol.tray.bootstrap;

import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.io.InputStream;

public class BootstrapVersionProviderImpl implements BootstrapVersionProvider {

	private volatile Integer bootstrapVersion;

	@Override
	public synchronized int current() {
		if (bootstrapVersion == null) {
			String value = "0";
			try (InputStream in = ResourcesHelper.class.getClassLoader().getResourceAsStream("bootstrap.version")) {
				if (in != null) {
					value = new String(in.readAllBytes()).trim();
				}
			}
			catch (IOException ignore) {
				// nothing to do. Value previously set
			}

			bootstrapVersion = NumberUtils.isCreatable(value) ? Integer.parseInt(value) : 0;
		}

		return bootstrapVersion;
	}

}
