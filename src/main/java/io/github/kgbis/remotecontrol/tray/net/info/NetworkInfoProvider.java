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
package io.github.kgbis.remotecontrol.tray.net.info;

import io.github.kgbis.remotecontrol.tray.net.internal.InfoListener;
import io.github.kgbis.remotecontrol.tray.ui.InformationScreen;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class NetworkInfoProvider implements InfoListener<InetAddress, String> {

	private final InformationScreen informationScreen;

	private Map<String, String> addresses;

	@Inject
    public NetworkInfoProvider(InformationScreen informationScreen) {
        this.informationScreen = informationScreen;
    }

    public String getMac(String ip) {
		return addresses.getOrDefault(ip, "");
	}

	public String getHostName(String ip) {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e) {
			return ip;
		}
	}

	public List<String> getIPv4Addresses() {
		return List.copyOf(addresses.keySet());
	}

	@Override
	public void onChange(Map<InetAddress, String> data) {
		this.addresses = data.entrySet()
			.stream()
			.collect(Collectors.toMap(k -> k.getKey().getHostAddress(), Map.Entry::getValue));
		informationScreen.onChange(addresses);
	}

}
