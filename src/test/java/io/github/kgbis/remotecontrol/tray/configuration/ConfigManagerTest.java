package io.github.kgbis.remotecontrol.tray.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfigManagerTest {

	@Mock
	ConfigStorage configStorage;

	@InjectMocks
	ConfigManager configManager;

	@Test
	void create_configuration_file_if_does_not_exist() throws IOException {
		Config createdConfig = Config.builder().build();

		when(configStorage.exists()).thenReturn(false);
		doNothing().when(configStorage).write(any(Config.class));

		Config config = configManager.current();

		assertEquals(createdConfig, config);
		verify(configStorage).write(any(Config.class));
	}

	@Test
	void read_configuration_file_if_exists() throws IOException {
		Config stored = Config.builder().appAutoStartOnLogin(true).build();

		when(configStorage.exists()).thenReturn(true);
		when(configStorage.read()).thenReturn(stored);

		Config config = configManager.current();

		verify(configStorage).read();
		assertEquals(stored, config);
	}

	@Test
	void return_defaults_if_read_fails() throws IOException {
		Config expected = Config.builder().build();

		when(configStorage.exists()).thenReturn(true);
		when(configStorage.read()).thenThrow(new IOException());

		Config config = configManager.current();

		assertEquals(expected, config);
		verify(configStorage).read();
	}

	@Test
	void save_configuration() throws IOException {
		Config config = Config.builder().build();
		configManager.save(config);
		verify(configStorage).write(config);
	}

	@Test
	void save_updates_cache() throws IOException {
		Config config = Config.builder().build();

		configManager.save(config);

		assertEquals(config, configManager.current());
	}

}