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

import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.Set;

public final class InformationTableRenderer {

	private final DefaultTableModel model;

	public InformationTableRenderer(DefaultTableModel model) {
		this.model = model;
	}

	public void render(Map<String, String> data) {
		model.setRowCount(0);
		data.forEach((ip, mac) -> model.addRow(new Object[] { ip, mac }));
	}

	public void render(Set<Device.DeviceInterface> interfaces) {
		model.setRowCount(0);
		interfaces.forEach(iface -> model.addRow(new Object[] { iface.getType(), iface.getIp(), iface.getMac() }));
	}

}
