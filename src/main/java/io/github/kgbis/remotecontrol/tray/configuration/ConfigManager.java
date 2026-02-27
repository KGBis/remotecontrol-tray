package io.github.kgbis.remotecontrol.tray.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigManager {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static Config currentConfig;

	public static Config load() throws IOException {
		Path configFile = ResourcesHelper.getConfigFile();

		if (!Files.exists(configFile)) {
			currentConfig = Config.builder().build();
			save(currentConfig);
			return currentConfig;
		}

		currentConfig = MAPPER.readValue(configFile.toFile(), Config.class);
		return currentConfig;
	}

	public static void save(Config config) throws IOException {
		Path configFile = ResourcesHelper.getConfigFile();
		MAPPER.writerWithDefaultPrettyPrinter().writeValue(configFile.toFile(), config);
		currentConfig = config;
	}

	public static Config current() {
		return currentConfig;
	}

}
