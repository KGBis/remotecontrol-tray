package io.github.kgbis.remotecontrol.tray.ui.settings;

import dorkbox.notify.Position;
import io.github.kgbis.remotecontrol.tray.configuration.Config;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Locale;

@ToString
@Builder(access = AccessLevel.PRIVATE)
public class SettingsModel {

	public static final int AUTOSTART_FEATURE = 1;

	public static final int LANGUAGE_FEATURE = 2;

	public static final int NOTIFICATIONS_FEATURE = 3;

	@Getter
	@Setter
	private boolean autostartEnabled;

	@Getter
	@Setter
	private Locale language;

	@Getter
	@Setter
	private boolean notificationsEnabled;

	@Getter
	@Setter
	private int notificationsDuration;

	@Getter
	@Setter
	private Position notificationPosition;

	public static SettingsModel of(Config config) {
		return SettingsModel.builder()
			.autostartEnabled(config.isAppAutoStartOnLogin())
			.language(config.getLocale())
			.notificationsEnabled(config.isShowNotifications())
			.notificationsDuration(config.getNotificationDuration())
			.notificationPosition(config.getNotificationPosition())
			.build();
	}

	public void applyTo(Config config) {
		config.setAppAutoStartOnLogin(autostartEnabled);
		config.setLocale(language);
		config.setShowNotifications(notificationsEnabled);
		config.setNotificationDuration(notificationsDuration);
		config.setNotificationPosition(notificationPosition);
	}

}
