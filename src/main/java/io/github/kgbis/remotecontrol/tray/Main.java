package io.github.kgbis.remotecontrol.tray;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.beust.jcommander.ParameterException;
import io.github.kgbis.remotecontrol.tray.cli.CliArguments;
import io.github.kgbis.remotecontrol.tray.cli.CliParser;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkChangeCallbackImpl;
import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import io.github.kgbis.remotecontrol.tray.net.server.NetworkServer;
import io.github.kgbis.remotecontrol.tray.ui.TrayBuilder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

import java.io.IOException;

@Slf4j
public class Main {

	public static final String REMOTE_PC_CONTROL = "Remote PC Control Tray";

	public static void main(String[] args) {
		try {
			CliArguments cliArguments = readArgsAndSetLogLevelIfNeeded(args);
			NetworkChangeCallbackImpl networkChangeCallback = new NetworkChangeCallbackImpl();
			new TrayBuilder(networkChangeCallback).loadTray();
			new NetworkServer(new NetworkInfoProvider(networkChangeCallback)).arguments(cliArguments).start();
		}
		catch (IOException e) {
			log.error("Something bad happened. Please report the following error: ", e);
			Thread.currentThread().interrupt();
			System.exit(-1);
		}
		catch (ParameterException parameterException) {
			parameterException.getJCommander().usage();
			System.exit(-2);
		}
	}

	private static CliArguments readArgsAndSetLogLevelIfNeeded(String[] args) {
		CliArguments cliArguments = CliParser.parseCommandLine(args);
        applyRootLogLevel(cliArguments.getLogLevel());
		return cliArguments;
	}

    private static void applyRootLogLevel(Level newLevel) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

        if (!root.getLevel().equals(newLevel)) {
            root.info("Changing ROOT log level from {} to {}", root.getLevel(), newLevel);
            root.setLevel(newLevel);
        }
    }


}