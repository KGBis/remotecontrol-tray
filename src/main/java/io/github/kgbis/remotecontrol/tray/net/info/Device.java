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

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

@Builder
@ToString
@Getter
public class Device {

	private UUID id;

	private String hostname;

	private DeviceInfo deviceInfo;

	@Builder.Default
	@Setter
	private Set<DeviceInterface> interfaces = new TreeSet<>(Comparator.comparing(DeviceInterface::getIp));

	@Builder
	@ToString
	@Getter
	public static class DeviceInfo {

		private String osName;

		private String osVersion;

		private String trayVersion;

	}

	@Builder
	@ToString
	@Getter
	public static class DeviceInterface {

		private String ip;

		private String mac;

		@Builder.Default
		private int port = 6800;

		private InterfaceType type;

	}

	public enum InterfaceType {

		ETHERNET, WIFI, UNKNOWN

	}

}
