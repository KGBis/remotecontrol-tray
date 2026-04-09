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
package io.github.kgbis.remotecontrol.tray.net.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class InfoNetworkAction extends NetworkAction<String> {

	private final NetworkInfoProvider provider;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@AssistedInject
	public InfoNetworkAction(NetworkInfoProvider networkInfoProvider, @Assisted Socket socket,
			@Assisted String[] args) {
		super(socket, args);
		this.provider = networkInfoProvider;
	}

	@Override
	public void execute() throws IOException {
		String ip = parseArguments();

		if (!deviceContainsIp(ip)) {
			log.warn("Unknown IP requested: {}", ip);
			writeToSocket(socket, "ERROR Unknown IP requested: " + ip);
			return;
		}

		String msg = objectMapper.writeValueAsString(provider.getDevice());
		log.debug("Responding with: {}", msg);
		writeToSocket(socket, msg);
	}

	private boolean deviceContainsIp(String ip) {
		return provider.getDevice().getInterfaces().stream().anyMatch(iface -> iface.getIp().equals(ip));
	}

	@Override
	protected String parseArguments() {
		if (args.length < 2)
			return null;
		return args[1];
	}

}
