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
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CliParserTest {

	@Test
	void shouldUseDefaultsWhenNoArgsProvided() {
		CliArguments args = CliParser.parseCommandLine(new String[] {}, JCommander::usage);

		assertEquals(Level.INFO, args.getLogLevel());
		assertFalse(args.isDryRun());
		assertFalse(args.isLogToConsole());
	}

	@Test
	void shouldParseLogLevelAndFlags() {
		String[] cmdLine = { "--logLevel", "WARN", "--dryRun" };
		CliArguments args = CliParser.parseCommandLine(cmdLine, JCommander::usage);

		assertEquals(Level.WARN, args.getLogLevel());
		assertTrue(args.isDryRun());
		assertFalse(args.isLogToConsole());
	}

	@Test
	void shouldFailOnInvalidLogLevel() {
		ParameterException ex = assertThrows(ParameterException.class,
				() -> CliParser.parseCommandLine(new String[] { "--logLevel", "FOO" }, JCommander::usage));

		assertTrue(ex.getMessage().contains("Invalid value"));
	}

}
