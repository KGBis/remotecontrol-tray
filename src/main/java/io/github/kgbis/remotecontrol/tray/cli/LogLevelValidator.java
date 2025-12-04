package io.github.kgbis.remotecontrol.tray.cli;

import ch.qos.logback.classic.Level;
import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import org.apache.commons.lang3.StringUtils;

public class LogLevelValidator implements IParameterValidator {

	@Override
	public void validate(String name, String value) throws ParameterException {
		try {
			Level.valueOf(StringUtils.upperCase(value));
		}
		catch (IllegalArgumentException e) {
			throw new ParameterException("Invalid value '" + value
					+ "'. Level must be one of the following values: OFF, TRACE, DEBUG, INFO, WARN, ERROR");
		}
	}

}
