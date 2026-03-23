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

import static org.junit.jupiter.api.Assertions.assertEquals;

class InformationTableModelUpdaterTest {

	@Test
	void render_updatesTableModel() {
		DefaultTableModel table = new DefaultTableModel(new Object[] { "TYPE", "IP", "MAC" }, 0);

		InformationTableModelUpdater renderer = new InformationTableModelUpdater(table);

		Device.DeviceInterface deviceInterface = Device.DeviceInterface.builder()
			.type(Device.InterfaceType.ETHERNET)
			.ip("192.168.1.10")
			.mac("00:11:22:33:44:55")
			.build();

		renderer.render(Set.of(deviceInterface));

		assertEquals(1, table.getRowCount());
		assertEquals(Device.InterfaceType.ETHERNET, table.getValueAt(0, 0));
		assertEquals("192.168.1.10", table.getValueAt(0, 1));
		assertEquals("00:11:22:33:44:55", table.getValueAt(0, 2));
	}

}
