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
package io.github.kgbis.remotecontrol.tray.ui.support;

import io.github.kgbis.remotecontrol.tray.net.info.Device;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public final class InformationModel {

	private final Map<String, String> addresses = new LinkedHashMap<>();

	private final AtomicReference<Device> device = new AtomicReference<>();

	public void update(Map<String, String> newData) {
		addresses.clear();
		addresses.putAll(newData);
	}

	public Map<String, String> getAddresses() {
		return Map.copyOf(addresses);
	}

	public Device getDevice() {
		return device.get() == null ? Device.builder().build() : device.get();
	}

	public int size() {
		return addresses.size();
	}

	public void update(Device device) {
		this.device.set(device);
	}

}
