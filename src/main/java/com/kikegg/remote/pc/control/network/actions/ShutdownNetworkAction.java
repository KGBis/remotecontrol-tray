package com.kikegg.remote.pc.control.network.actions;

import com.kikegg.remote.pc.control.network.server.NetworkInfoProvider;
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

	private final boolean debug;

	public ShutdownNetworkAction(Socket socket, String[] args, NetworkInfoProvider provider, boolean debug) {
		super(socket, args, provider);
		this.debug = debug;
	}

	@Override
	public void execute() throws IOException {
		ShutdownNetworkActionData request = parseArguments();
		if (request == null) {
			log.warn("Request arguments are wrong. args={}", Arrays.toString(args));
			writeToSocket(socket, "ERROR: invalid arguments");
			return;
		}

		long totalTimeInSeconds = request.getDelay() * request.getUnit().getDuration().getSeconds();
		String[] cmdLine = buildCommandLine(totalTimeInSeconds);

		log.info("Executing shutdown -> {}", StringUtils.join(cmdLine, " "));
		writeToSocket(socket, "ACK");

		if (!debug) {
			try {
				Runtime.getRuntime().exec(cmdLine);
			}
			catch (IOException e) {
				log.error("Error executing shutdown command", e);
			}
		}
		else {
			log.info("Debug mode ON: shutdown not executed");
		}
	}

    @SuppressWarnings("unchecked")
	protected ShutdownNetworkActionData parseArguments() {
		if (args.length < 3)
			return null;

		String delay = args[1];
		String unit = args[2];

		boolean isNumericDelay = NumberUtils.isCreatable(delay);
		boolean isTimeUnit = Arrays.stream(ChronoUnit.values())
			.anyMatch(chronoUnit -> chronoUnit.name().equalsIgnoreCase(unit));

		if (isNumericDelay && isTimeUnit) {
			return ShutdownNetworkActionData.builder()
				.delay(Integer.parseInt(delay))
				.unit(ChronoUnit.valueOf(unit.toUpperCase()))
				.build();
		}

		return null;
	}

	private String[] buildCommandLine(long timeInSeconds) {
		List<String> cmd = new ArrayList<>();

		if (SystemUtils.IS_OS_WINDOWS) {
			cmd.add("shutdown");
			cmd.add("-s");
			cmd.add("-t");
			cmd.add(String.valueOf(timeInSeconds));
		}
		else if (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_UNIX) {
			cmd.add("sudo");
			cmd.add("shutdown");
			cmd.add("-h");
			cmd.add(timeInSeconds == 0 ? "now" : "+" + (timeInSeconds / 60));
		}

		return cmd.toArray(new String[0]);
	}

}
