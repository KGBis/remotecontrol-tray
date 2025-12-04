package io.github.kgbis.remotecontrol.tray.cli;

import com.beust.jcommander.JCommander;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CliParser {

    public static CliArguments parseCommandLine(String[] args) {
        CliArguments cliArguments = new CliArguments();
        JCommander.newBuilder().addObject(cliArguments).build().parse(args);

        return cliArguments;
    }

}
