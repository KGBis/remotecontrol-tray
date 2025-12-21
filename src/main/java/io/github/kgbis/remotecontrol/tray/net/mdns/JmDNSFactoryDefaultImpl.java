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
package io.github.kgbis.remotecontrol.tray.net.mdns;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;

@Slf4j
@Singleton
public class JmDNSFactoryDefaultImpl implements JmDNSFactory {

	@Override
	public JmDNS create(InetAddress inetAddress) throws IOException {
		JmDNS jmDNS;
		try {
			jmDNS = JmDNS.create(inetAddress);
		}
		catch (BindException e) {
			log.warn("mDNS failed on {}, retrying default interface", inetAddress);
			jmDNS = JmDNS.create();
		}
		return jmDNS;
	}

}
