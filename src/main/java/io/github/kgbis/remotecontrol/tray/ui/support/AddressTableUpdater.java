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
import io.github.kgbis.remotecontrol.tray.net.internal.InfoListener;

import javax.swing.table.DefaultTableModel;

public class AddressTableUpdater implements InfoListener {

	private final DefaultTableModel model;

	public AddressTableUpdater(DefaultTableModel model) {
		this.model = model;
	}

	@Override
	public void onChange(Device device) {
		model.setRowCount(0);
		device.getInterfaces()
			.forEach(iface -> model.addRow(new Object[] { iface.getType(), iface.getIp(), iface.getMac() }));
	}

}
