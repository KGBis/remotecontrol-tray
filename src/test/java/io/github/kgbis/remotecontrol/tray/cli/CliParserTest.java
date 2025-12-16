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
