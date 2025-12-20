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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetworkInfoProviderTest {

	@Mock
	InetAddress inetAddress;

	@InjectMocks
	NetworkInfoProvider networkInfoProvider;

	@Test
	void testGetMacAndIPv4Addresses() {
		Map<InetAddress, String> data = Map.of(inetAddress, "AA:AA:AA:AA:AA:AA");

		when(inetAddress.getHostAddress()).thenReturn("10.0.0.1");
		networkInfoProvider.onChange(data);

		assertEquals("AA:AA:AA:AA:AA:AA", networkInfoProvider.getMac("10.0.0.1"));
		assertEquals(List.of("10.0.0.1"), networkInfoProvider.getIPv4Addresses());
	}

}
