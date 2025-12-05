package io.github.kgbis.remotecontrol.tray;

import com.beust.jcommander.ParameterException;
import io.github.kgbis.remotecontrol.tray.cli.CliArguments;
import io.github.kgbis.remotecontrol.tray.cli.CliParser;
import io.github.kgbis.remotecontrol.tray.logging.LogbackConfiguration;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeListener;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.server.NetworkServer;
import io.github.kgbis.remotecontrol.tray.ui.TrayBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {

	public static final String REMOTE_PC_CONTROL = "Remote PC Control Tray";

    public static final String APP_NAME = "RemoteControlTray";

	public static void main(String[] args) {
		try {
            // parse command line arguments (if -h or --help, show usage and exit)
			CliArguments cliArguments = CliParser.parseCommandLine(args);

            // configure logback system
			LogbackConfiguration.configure(cliArguments.getLogLevel(), cliArguments.isLogToConsole());

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
            System.err.println(parameterException.getMessage()); // NOSONAR
            parameterException.getJCommander().usage();
			System.exit(-2);
		}
	}

}