package com.kikegg.remote.shutdown;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class CMDWriter {

	private int timeInSeconds = 0;

	public void execShutdown(String clientMessage) {

		parseAndSetTime(clientMessage);
		String[] cmdLine = buildCommandLine();
		try {
			Runtime.getRuntime().exec(cmdLine);
		}
		catch (IOException e) {
			log.error("Error while shutting down system", e);
		}

	}

	private String[] buildCommandLine() {
		log.info("Shutdown timer: {} s", timeInSeconds);

		List<String> list = new ArrayList<>();
		list.add("shutdown"); // command

		// shutdown -s -t n (n = seconds)
		if (SystemUtils.IS_OS_WINDOWS) {
			list.add("-s");
			list.add("-t");
			list.add(String.valueOf(timeInSeconds));
		}

		// shutdown -h now | shutdown -h +m (m = minutes)
		if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_UNIX) {
			list.add(0, "sudo");
			list.add("-h");
			list.add((timeInSeconds == 0 ? "now" : "+" + (timeInSeconds / 60)));
		}

		return list.toArray(new String[] {});
	}

	private void parseAndSetTime(String clientMessage) {
		if (StringUtils.isBlank(clientMessage)) {
			return;
		}

		// Message is like 'GET /[timer][unit] HTTP/1.1'
		String cleanMsg = StringUtils.substring(clientMessage, 5, clientMessage.length() - 9);

		// Client MAY send no time unit if no unit radio button was selected in mobile
		// app. Will look like "0null"
		String timeParam = StringUtils.replace(StringUtils.trim(cleanMsg), "null", "s");

		// Trim and extract time suffix ("s" = seconds, "m" = minutes or "h" = hours)
		String[] strings = StringUtils.splitByCharacterType(timeParam);
		if (strings.length != 2) {
			return;
		}
		String timerStr = strings[0];
		String unit = strings[1].toLowerCase();

		if (NumberUtils.isCreatable(timerStr)) {
			int timer = Integer.parseInt(timerStr);
			switch (unit) {
				case "m":
					timeInSeconds = 60 * timer;
					break; // Minutes to seconds
				case "h":
					timeInSeconds = 3600 * timer;
					break; // hours to seconds
				case "s":
				default:
					timeInSeconds = timer;
			}
		}
		else {
			log.warn("Received param is wrong, could not get shutdown delay. Using lastest: {}", timeInSeconds);
		}
	}

}
