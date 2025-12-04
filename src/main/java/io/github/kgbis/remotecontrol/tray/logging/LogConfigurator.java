package io.github.kgbis.remotecontrol.tray.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class LogConfigurator {

	private static final String APP_NAME = "RemoteControlTray";

	private static final String DEFAULT_PATTERN = "%date{ISO8601} %-25(%-5p [%t]) %logger{1} - %m%n";

	public static void configure(Level rootLevel) {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		// Reset context to avoid logback.xml auto-loading
		context.reset();

		String logDir = getOSLogDirectory();

		// ---------------------
		// Console appender
		// ---------------------
		ConsoleAppender<ILoggingEvent> console = new ConsoleAppender<>();
		console.setContext(context);
		PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
		consoleEncoder.setContext(context);
		consoleEncoder.setPattern(DEFAULT_PATTERN);
		consoleEncoder.start();
		console.setEncoder(consoleEncoder);
		console.start();

		// ---------------------
		// File appender with rolling policy
		// ---------------------
		new File(logDir).mkdirs(); // create dirs if needed

		String filename = StringUtils.join(logDir, "/", APP_NAME, ".log");
		String gz = StringUtils.join(logDir, "/", APP_NAME, "-%d{yyyy-MM-dd}.gz");

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
		fileEncoder.setPattern(DEFAULT_PATTERN);
		fileEncoder.start();

		fileAppender.setEncoder(fileEncoder);
		fileAppender.start();

		// ---------------------
		// Root logger
		// ---------------------
		Logger root = context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		root.addAppender(console);
		root.addAppender(fileAppender);
		root.setLevel(Level.INFO);
		log.info("Logging initialized. Root level: {}", rootLevel);
		root.setLevel(rootLevel);
	}

	/* @formatter:off
	TODO Append paths using Path.get
	TODO like: Path filename = Paths.get(logDir, APP_NAME + ".log");
    TODO       Path gz = Paths.get(logDir, APP_NAME + "-%d{yyyy-MM-dd}.gz");
	*/
    // @formatter:on

	private static String getOSLogDirectory() {
		String userHome = System.getProperty("user.home");
		String os = System.getProperty("os.name").toLowerCase();

		String logDir;
		if (os.contains("win")) {
			String appData = System.getenv("APPDATA");
			logDir = appData != null ? appData + "\\" + APP_NAME + "\\logs" : userHome + "\\" + APP_NAME + "\\logs";
		}
		else if (os.contains("mac")) {
			logDir = userHome + "/Library/Logs/" + APP_NAME;
		}
		else {
			// Linux/Unix
			logDir = userHome + "/.config/" + APP_NAME + "/logs";
		}

		// Create directories if they don't exist
		File dir = new File(logDir);
		if (!dir.exists())
			dir.mkdirs();

		return logDir;
	}

}
