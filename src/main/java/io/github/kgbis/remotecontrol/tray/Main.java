package io.github.kgbis.remotecontrol.tray;

import com.beust.jcommander.ParameterException;
import io.github.kgbis.remotecontrol.tray.cli.CliArguments;
import io.github.kgbis.remotecontrol.tray.cli.CliParser;
import io.github.kgbis.remotecontrol.tray.logging.LogConfigurator;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeListener;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.server.NetworkServer;
import io.github.kgbis.remotecontrol.tray.ui.TrayBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {

	public static final String REMOTE_PC_CONTROL = "Remote PC Control Tray";

	public static void main(String[] args) {
		try {
			CliArguments cliArguments = CliParser.parseCommandLine(args);
			LogConfigurator.configure(cliArguments.getLogLevel());

			NetworkChangeListener networkChangeListener = new NetworkChangeListener();
			new TrayBuilder(networkChangeListener).loadTray();
			new NetworkServer(new NetworkInfoProvider(networkChangeListener)).arguments(cliArguments).start();
		}
		catch (IOException | InterruptedException e) {
			log.error("Something bad happened. Please report the following error: ", e);
			Thread.currentThread().interrupt();
			System.exit(-1);
		}
		catch (ParameterException parameterException) {
			parameterException.getJCommander().usage();
			System.exit(-2);
		}
	}

}