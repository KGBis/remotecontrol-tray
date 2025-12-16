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
