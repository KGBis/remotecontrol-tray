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
package io.github.kgbis.remotecontrol.tray.cli;

import ch.qos.logback.classic.Level;
import com.beust.jcommander.Parameter;
import lombok.Getter;

@Getter
public class CliArguments {

	private static final String LEVELS = "Logging levels. OFF, TRACE, DEBUG, INFO, WARN, ERROR";

	@Parameter(names = { "-d", "--dryRun" }, description = "Dry run mode. No shutdown will be performed")
	private boolean dryRun = false;

	@Parameter(names = { "-l", "--logLevel" }, arity = 1, description = LEVELS, converter = LogLevelConverter.class,
			validateWith = LogLevelValidator.class)
	private Level logLevel = Level.INFO;

	@Parameter(names = { "-c", "--console" }, description = "Log to console. Not to be used for release")
	private boolean logToConsole = false;

	@Parameter(names = { "-h", "--help", "-u", "--usage" }, help = true, description = "Show this usage help")
	private boolean help;

}
