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

    @Parameter(names = { "-c", "--logToConsole" }, description = "Log to console. Not to be used for release")
    private boolean logToConsole = false;

	@Parameter(names = { "-h", "--help", "-u", "--usage" }, help = true, description = "Show this usage help")
	private boolean help;

}
