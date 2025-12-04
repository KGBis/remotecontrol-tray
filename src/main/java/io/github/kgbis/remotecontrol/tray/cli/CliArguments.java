package io.github.kgbis.remotecontrol.tray.cli;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import ch.qos.logback.classic.Level;

@Getter
public class CliArguments {

	private static final String LEVELS = "Logging levels. OFF, TRACE, DEBUG, INFO, WARN, ERROR";

	@Parameter(names = { "-d", "--dryRun" }, description = "Dry run mode. No shutdown will be performed")
	private boolean dryRun = false;

	@Parameter(names = { "-l", "--logLevel" }, description = LEVELS, placeholder = "<logLevel>",
			converter = LogLevelConverter.class, validateWith = LogLevelValidator.class)
	private Level logLevel = Level.INFO;

}
