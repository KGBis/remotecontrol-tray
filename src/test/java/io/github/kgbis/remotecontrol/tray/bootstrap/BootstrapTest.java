package io.github.kgbis.remotecontrol.tray.bootstrap;

import io.github.kgbis.remotecontrol.tray.autostart.AutoStartController;
import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.ui.support.FirstRunDialogHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootstrapTest {

	@Mock
	ConfigManager configManager;

	@Mock
	AutoStartController autoStartController;

	@Mock
	FirstRunDialogHandler firstRunHandler;

	@Mock
	BootstrapVersionProvider versionProvider;

	@InjectMocks
	Bootstrap bootstrap;

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void no_config_firstRun_accepts_autostart(boolean value) throws Exception {
		ArgumentCaptor<Config> captor = ArgumentCaptor.forClass(Config.class);

		Config config = new Config();

		when(configManager.current()).thenReturn(config);
		when(firstRunHandler.run()).thenReturn(new BootstrapAutoStart(value));
		when(versionProvider.current()).thenReturn(1);

		bootstrap.execute();

		verify(configManager).save(any(Config.class));
		verify(autoStartController).syncAutoStart(value);

		verify(configManager).save(captor.capture());
		Config saved = captor.getValue();
		assertEquals(value, saved.isAppAutoStartOnLogin());
		assertEquals(1, saved.getOnboardingVersion());
	}

	@Test
	void upToDate_config_does_not_run_autostart() throws IOException {
		Config config = Config.builder().onboardingVersion(3).build();

		when(configManager.current()).thenReturn(config);
		when(versionProvider.current()).thenReturn(3);

		bootstrap.execute();

		verify(firstRunHandler, never()).run();
		verify(configManager, never()).save(any());
		verify(autoStartController).syncAutoStart(config.isAppAutoStartOnLogin());
	}

	@Test
	void version_change_runs_onBoarding() throws IOException {
		Config config = Config.builder().onboardingVersion(2).build();

		when(configManager.current()).thenReturn(config);
		when(versionProvider.current()).thenReturn(3);
		when(firstRunHandler.run()).thenReturn(new BootstrapAutoStart(true));

		bootstrap.execute();

		verify(firstRunHandler).run();
	}

}
