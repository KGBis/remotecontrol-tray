package io.github.kgbis.remotecontrol.tray.cli;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class LogLevelValidator implements IParameterValidator {

	private static final Set<String> VALID_LEVELS = Set.of("OFF", "TRACE", "DEBUG", "INFO", "WARN", "ERROR");

	@Override
	public void validate(String name, String value) throws ParameterException {
		if (!VALID_LEVELS.contains(StringUtils.upperCase(value))) {
			throw new ParameterException("Invalid value '" + value + "'. Level must be one of: " + VALID_LEVELS);
		}
	}

}
