package io.github.kgbis.remotecontrol.tray.cli;

import com.beust.jcommander.JCommander;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static io.github.kgbis.remotecontrol.tray.Main.APP_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CliParser {

	public static CliArguments parseCommandLine(String[] args) {
		CliArguments cliArguments = new CliArguments();
		JCommander jCommander = JCommander.newBuilder()
			.programName(APP_NAME)
			.allowAbbreviatedOptions(true)
			.addObject(cliArguments)
			.build();
		jCommander.setCaseSensitiveOptions(false);
		jCommander.parse(args);

		if (cliArguments.isHelp()) {
			jCommander.usage();
			System.exit(0);
		}

		return cliArguments;
	}

}
