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

import com.beust.jcommander.JCommander;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.APP_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CliParser {

	public static CliArguments parseCommandLine(String[] args, Consumer<JCommander> onHelp) {
		CliArguments cliArguments = new CliArguments();
		JCommander jCommander = JCommander.newBuilder()
			.programName(APP_NAME)
			.allowAbbreviatedOptions(true)
			.addObject(cliArguments)
			.build();
		jCommander.setCaseSensitiveOptions(false);
		jCommander.parse(args);

		if (cliArguments.isHelp()) {
			onHelp.accept(jCommander);
		}

		return cliArguments;
	}

}
