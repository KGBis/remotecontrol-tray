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
package io.github.kgbis.remotecontrol.tray.net.info;

import io.github.kgbis.remotecontrol.tray.net.internal.InfoListener;
import io.github.kgbis.remotecontrol.tray.ui.InformationScreen;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

@Singleton
@Slf4j
public class NetworkInfoProvider implements InfoListener {

	private final InformationScreen informationScreen;

	@Getter
	private Device device;

	@Inject
	public NetworkInfoProvider(InformationScreen informationScreen) {
		this.informationScreen = informationScreen;
	}

	public String getHostName(String ip) {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e) {
			return ip;
		}
	}

	@Override
	public void onChange(Device device) {
		this.device = device;
		informationScreen.onChange(device);
	}

}
