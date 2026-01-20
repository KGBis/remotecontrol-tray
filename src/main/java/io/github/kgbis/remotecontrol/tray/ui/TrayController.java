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
package io.github.kgbis.remotecontrol.tray.ui;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class TrayController {

	private final InformationScreen informationScreen;

	@Inject
	public TrayController(InformationScreen informationScreen) {
		this.informationScreen = informationScreen;
	}

	public void toggleWindow() {
		if (informationScreen.isVisible()) {
			informationScreen.hide();
		}
		else {
			informationScreen.show();
		}
	}

}
