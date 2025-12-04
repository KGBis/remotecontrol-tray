package io.github.kgbis.remotecontrol.tray.cli;

import com.beust.jcommander.IStringConverter;
import ch.qos.logback.classic.Level;

public class LogLevelConverter implements IStringConverter<Level> {

    @Override
    public Level convert(String value) {
        return Level.valueOf(value);
    }
}
