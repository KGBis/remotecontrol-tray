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
import org.junit.jupiter.api.Test;

import javax.swing.table.DefaultTableModel;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTableUpdaterTest {

	@Test
	void onChange_updatesTableModel() {
		DefaultTableModel model = new DefaultTableModel(new Object[] { "Type", "IP", "MAC" }, 0);
		AddressTableUpdater updater = new AddressTableUpdater(model);

		Device device = Device.builder()
			.id(UUID.randomUUID())
			.hostname("Hostname")
			.deviceInfo(
					Device.DeviceInfo.builder().osName("Windows 11").osVersion("10.0").trayVersion("2026.01.1").build())
			.interfaces(Set.of(Device.DeviceInterface.builder()
				.ip("10.0.0.2")
				.mac("22:2A:00:2A:A2:22")
				.port(6800)
				.type(Device.InterfaceType.WIFI)
				.build()))
			.build();

		updater.onChange(device);

		assertEquals(1, model.getRowCount());
		assertEquals(Device.InterfaceType.WIFI, model.getValueAt(0, 0));
		assertEquals("10.0.0.2", model.getValueAt(0, 1));
		assertEquals("22:2A:00:2A:A2:22", model.getValueAt(0, 2));
	}

}
