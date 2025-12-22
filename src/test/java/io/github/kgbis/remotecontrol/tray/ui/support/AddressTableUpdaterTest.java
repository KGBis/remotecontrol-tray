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

import org.junit.jupiter.api.Test;

import javax.swing.table.DefaultTableModel;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTableUpdaterTest {

	@Test
	void onChange_updatesTableModel() {
		DefaultTableModel model = new DefaultTableModel(new Object[] { "IP", "MAC" }, 0);

		AddressTableUpdater updater = new AddressTableUpdater(model);

		Map<String, String> data = Map.of("192.168.1.10", "00:11:22:33:44:55");

		updater.onChange(data);

		assertEquals(1, model.getRowCount());
		assertEquals("192.168.1.10", model.getValueAt(0, 0));
		assertEquals("00:11:22:33:44:55", model.getValueAt(0, 1));
	}

}
