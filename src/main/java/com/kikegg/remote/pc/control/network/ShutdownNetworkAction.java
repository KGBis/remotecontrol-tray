package com.kikegg.remote.pc.control.network;

import com.kikegg.remote.pc.control.model.ShutdownRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.net.Socket;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ShutdownNetworkAction extends NetworkAction {

	public ShutdownNetworkAction(Socket socket, String[] args) {
		super(socket, args);
	}

	@Override
	public void execute() throws IOException {
		ShutdownRequest request = parseArguments();

		if (request == null) {
			log.warn("Request arguments are wrong. delay={}, unit={}", args[1], args[2]);
			return;
		}

		long durationInSeconds = request.getUnit().getDuration().getSeconds();
		long totalTimeInSeconds = request.getDelay() * durationInSeconds;

		String[] cmdLine = buildCommandLine(totalTimeInSeconds);

		// noinspection ConfusingArgumentToVarargsMethod
		log.info("Executing -> {}", StringUtils.joinWith(" ", cmdLine));
		writeToSocket(socket, "ACK");
		// Runtime.getRuntime().exec(cmdLine);
	}

	private ShutdownRequest parseArguments() {
		String delay = args[1];
		String unit = args[2];

		boolean isNumericDelay = NumberUtils.isCreatable(delay);
		boolean isTimeUnit = Arrays.stream(ChronoUnit.values()).anyMatch(timeUnit -> timeUnit.name().equals(unit));

		if (isNumericDelay && isTimeUnit) {
			return ShutdownRequest.builder().delay(Integer.parseInt(delay)).unit(ChronoUnit.valueOf(unit)).build();
		}

		return null;
	}

	private String[] buildCommandLine(long timeInSeconds) {
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

}
