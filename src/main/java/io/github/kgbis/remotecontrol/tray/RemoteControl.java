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
package io.github.kgbis.remotecontrol.tray;

import com.beust.jcommander.ParameterException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.kgbis.remotecontrol.tray.cli.CliArguments;
import io.github.kgbis.remotecontrol.tray.cli.CliParser;
import io.github.kgbis.remotecontrol.tray.ioc.RemoteControlModule;
import io.github.kgbis.remotecontrol.tray.logging.LogbackConfiguration;
import io.github.kgbis.remotecontrol.tray.net.server.NetworkServer;
import io.github.kgbis.remotecontrol.tray.ui.TrayManager;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.UIManager;
import java.io.IOException;
import java.net.BindException;

import static io.github.kgbis.remotecontrol.tray.net.server.NetworkServer.PORT;

@Singleton
@Slf4j
public class RemoteControl {

	public static final String REMOTE_PC_CONTROL = "Remote PC Control Tray";

	public static final String APP_NAME = "RemoteControlTray";

	private final NetworkServer networkServer;

	private final TrayManager trayManager;

	@Inject
	public RemoteControl(NetworkServer networkServer, TrayManager trayManager) {
		this.networkServer = networkServer;
		this.trayManager = trayManager;
	}

	public void start(CliArguments cliArgs) throws IOException {
		trayManager.initializeTray();
		networkServer.arguments(cliArgs).start();
	}

	public static void main(String[] args) {
		// To fix blurry fonts on Linux
		if (SystemUtils.IS_OS_UNIX) {
			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ignored) {
			// don't care about any exception here
		}

		try {
			// parse command line arguments (if -h or --help, show usage and exit)
			CliArguments cliArguments = CliParser.parseCommandLine(args, jc -> {
				jc.usage();
				System.exit(0);
			});

			// configure logback system
			LogbackConfiguration.configure(cliArguments.getLogLevel(), cliArguments.isLogToConsole());

			// Start Guice DI container and entrypoint class (RemoteControl)
			Injector injector = Guice.createInjector(new RemoteControlModule());
			injector.getInstance(RemoteControl.class).start(cliArguments);
		}
		catch (BindException be) {
			log.error("Error while binding port to " + PORT + ". Check if already in use!");
			System.exit(1);
		}
		catch (IOException e) {
			log.error("Something bad happened. Please report the following error: ", e);
			Thread.currentThread().interrupt();
			System.exit(-1);
		}
		catch (ParameterException parameterException) {
			System.err.println(parameterException.getMessage()); // NOSONAR
			parameterException.getJCommander().usage();
			System.exit(-2);
		}
	}

}