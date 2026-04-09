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
package io.github.kgbis.remotecontrol.tray.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dorkbox.notify.Position;
import io.github.kgbis.remotecontrol.tray.ui.support.NotificationDuration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.SystemUtils;

import java.util.Locale;

@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {

	@Getter
	@Setter
	@Builder.Default
	private int onboardingVersion = 0;

	@Getter
	@Setter
	@Builder.Default
	private boolean appAutoStartOnLogin = false;

	@Getter
	@Setter
	@Builder.Default
	private Locale locale = Locale.ENGLISH;

	@Getter
	@Setter
	@Builder.Default
	private boolean showNotifications = !SystemUtils.IS_OS_WINDOWS;

	@Getter
	@Setter
	@Builder.Default
	private int notificationDuration = NotificationDuration.MEDIUM.getTtl(); // millis

	@Getter
	@Setter
	@Builder.Default
	private Position notificationPosition = Position.BOTTOM_RIGHT;

	public boolean isInitialized(int expectedVersion) {
		return onboardingVersion == expectedVersion;
	}

}
