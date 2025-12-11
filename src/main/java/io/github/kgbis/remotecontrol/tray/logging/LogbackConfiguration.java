package io.github.kgbis.remotecontrol.tray.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.APP_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class LogbackConfiguration {

	private static final String LOG_FILE_NAME = APP_NAME + ".log";

	private static final String GZ_FILE_NAME = LOG_FILE_NAME + "-%d{yyyy-MM-dd}.gz";

	private static final String LOGGING_PATTERN = "%date{ISO8601} %-25(%-5p [%t]) %logger{1} - %m%n";

	public static void configure(Level rootLevel, boolean logToConsole) {
		Path logDir = getOSLogDirectory();

		// Logger context
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		context.reset();

		// Rolling file appender
		String filename = Paths.get(logDir.toString(), LOG_FILE_NAME).toString();
		String gz = Path.of(logDir.toString(), GZ_FILE_NAME).toString();

		RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
		fileAppender.setContext(context);
		fileAppender.setFile(filename);

		TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
		rollingPolicy.setContext(context);
		rollingPolicy.setParent(fileAppender);
		rollingPolicy.setFileNamePattern(gz);
		rollingPolicy.setMaxHistory(30);
		rollingPolicy.start();

		fileAppender.setRollingPolicy(rollingPolicy);

		PatternLayoutEncoder fileEncoder = new PatternLayoutEncoder();
		fileEncoder.setContext(context);
		fileEncoder.setPattern(LOGGING_PATTERN);
		fileEncoder.start();

		fileAppender.setEncoder(fileEncoder);
		fileAppender.start();

		// oshi logger (too much logs on Linux when in DEBUG)
		Logger oshi = context.getLogger("oshi");
		if (rootLevel.equals(Level.DEBUG) || rootLevel.equals(Level.TRACE)) {
			oshi.setLevel(Level.INFO);
		}

		// Root logger
		Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		if (logToConsole)
			root.addAppender(consoleAppender(context));
		root.addAppender(fileAppender);
		root.setLevel(Level.INFO);
		log.info("Starting {} version {}", APP_NAME, ResourcesHelper.getVersion());
		log.info("Logging initialized. Root level: {}", rootLevel);
		root.setLevel(rootLevel);
	}

	private static ConsoleAppender<ILoggingEvent> consoleAppender(LoggerContext context) {
		// Console appender
		ConsoleAppender<ILoggingEvent> console = new ConsoleAppender<>();
		console.setContext(context);
		PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
		consoleEncoder.setContext(context);
		consoleEncoder.setPattern(LOGGING_PATTERN);
		consoleEncoder.start();
		console.setEncoder(consoleEncoder);
		console.start();

		return console;
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static Path getOSLogDirectory() {
		String userHome = System.getProperty("user.home");
		String os = System.getProperty("os.name").toLowerCase();

		Path logDir;
		if (os.contains("win")) {
			String appData = System.getenv("APPDATA");
			logDir = appData != null ? Path.of(appData, APP_NAME, "logs") : Path.of(userHome, APP_NAME, "logs");
		}
		else if (os.contains("mac")) {
			logDir = Path.of(userHome, "Library", "Logs", APP_NAME);
		}
		else {
			// Linux/Unix
			logDir = Path.of(userHome, ".config", APP_NAME, "logs");
		}

		// Create directories if they don't exist
		File dir = new File(logDir.toUri());
		if (!dir.exists()) {
			dir.mkdirs();
		}

		return logDir;
	}

}
