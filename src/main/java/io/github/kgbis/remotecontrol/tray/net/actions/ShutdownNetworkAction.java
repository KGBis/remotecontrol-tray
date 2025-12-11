package io.github.kgbis.remotecontrol.tray.net.actions;

import io.github.kgbis.remotecontrol.tray.net.info.NetworkInfoProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ShutdownNetworkAction extends NetworkAction {

	private final boolean isDryRun;

	public ShutdownNetworkAction(Socket socket, String[] args, NetworkInfoProvider provider, boolean isDryRun) {
		super(socket, args, provider);
		this.isDryRun = isDryRun;
	}

	@Override
	public void execute() throws IOException {
		ShutdownNetworkActionData request = parseArguments();
		if (request == null) {
			log.warn("Request arguments are wrong. args={}", Arrays.toString(args));
			writeToSocket(socket, "ERROR invalid arguments");
			return;
		}

		long totalTimeInSeconds = request.getDelay() * request.getUnit().getDuration().getSeconds();
		String[] cmdLine = buildCommandLine(totalTimeInSeconds);

		log.info("Executing shutdown -> {}", StringUtils.join(cmdLine, " "));
		writeToSocket(socket, "ACK");

		if (!isDryRun) {
			try {
				ProcessBuilder builder = new ProcessBuilder(cmdLine);

				// Redirect the error stream to the output stream to ensure all output is
				// captured
				builder.redirectErrorStream(true);
				Process process = builder.start();

				// Get the input stream (which now includes standard error due to
				// redirectErrorStream(true))
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						log.debug(line);
					}
				}

				// Wait for the process to complete and get the exit code
				int exitCode = process.waitFor();
				log.debug("shutdown exit code: {}", exitCode);
			}
			catch (IOException e) {
				log.error("Error executing shutdown command", e);
			}
			catch (InterruptedException e) {
				log.error("Error executing shutdown command", e);
				Thread.currentThread().interrupt();
			}
		}
		else {
			log.info("DryRun mode ON: shutdown not executed");
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
			// cmd.add("sudo"); // using 'sudo' requires human intervention ;)
			// shutdown command uses time in minutes
			String sdTime = "now";
			int minutes = Math.toIntExact(timeInSeconds / 60);
			if (minutes > 0) {
				sdTime = "+" + minutes;
			}

			cmd.add("shutdown");
			cmd.add("-h");
			cmd.add(sdTime);
		}

		return cmd.toArray(new String[0]);
	}

}
