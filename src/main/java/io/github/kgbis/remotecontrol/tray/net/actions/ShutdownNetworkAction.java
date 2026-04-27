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

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import io.github.kgbis.remotecontrol.tray.misc.RuntimeConfig;
import io.github.kgbis.remotecontrol.tray.ui.support.ActionDesktopNotifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.net.Socket;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ShutdownNetworkAction extends NetworkAction<ShutdownNetworkActionData> {

	private final boolean isDryRun;

	private final ActionDesktopNotifier desktopNotifier;

	@AssistedInject
	public ShutdownNetworkAction(RuntimeConfig runtimeConfig, ActionDesktopNotifier desktopNotifier,
			@Assisted Socket socket, @Assisted String[] args) {
		super(socket, args);
		this.isDryRun = runtimeConfig.isDryRun();
		this.desktopNotifier = desktopNotifier;
	}

	@Override
	public void execute() throws IOException {
		ShutdownNetworkActionData request = parseArguments();
		if (request == null) {
			log.warn("Request arguments are wrong. args={}", Arrays.toString(args));
			writeToSocket(socket, "ERROR invalid arguments");
			return;
		}

		long totalTimeInSeconds = request.getDelay() * request.getUnit().getDuration().getSeconds();
		String[] cmdLine = buildCommandLine(totalTimeInSeconds);

		log.info("Executing shutdown -> {}", StringUtils.join(cmdLine, " "));

		int exitCode = 0;

		if (!isDryRun) {
			exitCode = execute(cmdLine);
		}
		else {
			log.info("DryRun mode ON: shutdown not executed");
		}

		// show notification
		if (exitCode == 0) {
			desktopNotifier.notifyShutdown(request);
		}

		writeToSocket(socket, exitCode == 0 ? "ACK" : "ERROR " + exitCode);
	}

	protected ShutdownNetworkActionData parseArguments() {
		if (args.length < 3)
			return null;

		String delay = args[1];
		String unit = args[2];

		boolean isNumericDelay = NumberUtils.isCreatable(delay);
		boolean isTimeUnit = Arrays.stream(ChronoUnit.values())
			.anyMatch(chronoUnit -> chronoUnit.name().equalsIgnoreCase(unit));

		if (isNumericDelay && isTimeUnit) {
			return ShutdownNetworkActionData.builder()
				.delay(Integer.parseInt(delay))
				.unit(ChronoUnit.valueOf(unit.toUpperCase()))
				.build();
		}

		return null;
	}

	private String[] buildCommandLine(long timeInSeconds) {
		List<String> cmd = new ArrayList<>();

		if (SystemUtils.IS_OS_WINDOWS) {
			cmd.add("shutdown");
			cmd.add("-s");
			cmd.add("-t");
			cmd.add(String.valueOf(timeInSeconds));
		}
		else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_LINUX) {
			// cmd.add("sudo"); // using 'sudo' requires human intervention ;)
			// shutdown command uses time in minutes
			String sdTime = "now";
			int minutes = Math.toIntExact(timeInSeconds / 60);
			if (minutes > 0) {
				sdTime = "+" + minutes;
			}

			cmd.add("shutdown");
			cmd.add("-h");
			cmd.add(sdTime);
		}

		return cmd.toArray(new String[0]);
	}

}
