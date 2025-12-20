/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.cli;

import ch.qos.logback.classic.Level;
import com.beust.jcommander.IStringConverter;

public class LogLevelConverter implements IStringConverter<Level> {

	@Override
	public Level convert(String value) {
		return Level.valueOf(value);
	}

}
