
package io.github.kgbis.remotecontrol.tray.cli;

import com.beust.jcommander.ParameterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogLevelValidatorTest {

	private final LogLevelValidator validator = new LogLevelValidator();

	@Test
	void shouldAcceptValidLogLevels() {
		assertDoesNotThrow(() -> validator.validate("--logLevel", "OFF"));
		assertDoesNotThrow(() -> validator.validate("--logLevel", "TRACE"));
		assertDoesNotThrow(() -> validator.validate("--logLevel", "DEBUG"));
		assertDoesNotThrow(() -> validator.validate("--logLevel", "INFO"));
		assertDoesNotThrow(() -> validator.validate("--logLevel", "WARN"));
		assertDoesNotThrow(() -> validator.validate("--logLevel", "ERROR"));
	}

	@Test
	void shouldAcceptLowercaseLogLevels() {
		assertDoesNotThrow(() -> validator.validate("--logLevel", "off"));
		assertDoesNotThrow(() -> validator.validate("--logLevel", "trace"));
		assertDoesNotThrow(() -> validator.validate("--logLevel", "debug"));
		assertDoesNotThrow(() -> validator.validate("--logLevel", "info"));
		assertDoesNotThrow(() -> validator.validate("--logLevel", "warn"));
		assertDoesNotThrow(() -> validator.validate("--logLevel", "error"));
	}

	@Test
	void shouldRejectInvalidLogLevel() {
		ParameterException ex = assertThrows(ParameterException.class,
				() -> validator.validate("--logLevel", "VERBOSE"));

		assertTrue(ex.getMessage().contains("Invalid value"));
		assertTrue(ex.getMessage().contains("OFF"));
		assertTrue(ex.getMessage().contains("ERROR"));
	}

	@Test
	void shouldRejectEmptyValue() {
		assertThrows(ParameterException.class, () -> validator.validate("--logLevel", ""));
	}

}
