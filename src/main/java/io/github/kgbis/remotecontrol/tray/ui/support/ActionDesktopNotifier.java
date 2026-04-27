/*
 * Remote PC Control
 * Copyright (C) 2026 Enrique García (https://github.com/KGBis)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.ui.support;

import com.jthemedetecor.OsThemeDetector;
import dorkbox.notify.Notify;
import dorkbox.notify.Position;
import dorkbox.notify.Theme;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.misc.RuntimeConfig;
import io.github.kgbis.remotecontrol.tray.net.actions.ShutdownNetworkActionData;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;

@Slf4j
@Singleton
public class ActionDesktopNotifier {

	private final OsThemeDetector detector;

	private final I18nService i18nService;

	private final ConfigManager configManager;

	private final RuntimeConfig runtimeConfig;

	@Inject
	public ActionDesktopNotifier(I18nService i18nService, ConfigManager configManager, RuntimeConfig runtimeConfig,
			OsThemeDetector detector) {
		this.i18nService = i18nService;
		this.configManager = configManager;
		this.runtimeConfig = runtimeConfig;
		this.detector = detector;

		setNotifyFont();
	}

	/**
	 * Previews notification with a sample text in the position the user selected
	 * @param pos Area where notification is shown.
	 * @param time Duration of the notification. When 0, close manually, it's changed to
	 * {@link NotificationDuration#SHORT} duration.
	 * @param title internationalized notification text.
	 */
	public void preview(Position pos, int time, String title) {
		String text = """
				Lorem ipsum dolor sit amet, consectetur adipiscing elit,
				sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
				""";

		SwingUtilities.invokeLater(() -> Notify.Companion.create()
			.title(title)
			.text(text)
			.theme(getTheme())
			.position(pos)
			.hideAfter(time == 0 ? NotificationDuration.SHORT.getTtl() : time)
			.show());
	}

	public void notifyShutdown(ShutdownNetworkActionData request) {
		if (configManager.current().isShowNotifications()) {
			NotificationData data = buildShutdownNotification(request);
			SwingUtilities.invokeLater(() -> showNotification(data));
		}
	}

	public void notifyCancelShutdown() {
		if (configManager.current().isShowNotifications()) {
			NotificationData data = buildCancelShutdownNotification();
			SwingUtilities.invokeLater(() -> showNotification(data));
		}
	}

	void setNotifyFont() {
		Font font = UIManager.getFont("Label.font");
		String textFont = StringUtils.joinWith(" ", font.getFamily(), "PLAIN", font.getSize());
		String titleFont = StringUtils.joinWith(" ", font.getFamily(), "BOLD", font.getSize());

		Notify.Companion.setMAIN_TEXT_FONT(textFont);
		Notify.Companion.setTITLE_TEXT_FONT(titleFont);
	}

	private Theme getTheme() {
		return detector.isDark() ? Theme.Companion.getDefaultDark() : Theme.Companion.getDefaultLight();
	}

	private void showNotification(NotificationData data) {
		Notify.Companion.create()
			.title(data.title())
			.text(data.text())
			.theme(data.theme())
			.position(data.position())
			.hideAfter(data.duration())
			.show();
	}

	private NotificationData buildShutdownNotification(ShutdownNetworkActionData request) {
		Theme theme = getTheme();
		int notificationDuration = configManager.current().getNotificationDuration();
		Position position = configManager.current().getNotificationPosition();

		String text;

		if (runtimeConfig.isDryRun()) {
			text = i18nService.get("notification.shutdown.dryrun");
		}
		else {
			if (request.getDelay() == 0) {
				text = i18nService.get("notification.shutdown.now"); // No delay
			}
			else {
				ZonedDateTime now = ZonedDateTime.now();
				ZonedDateTime start = now.plus(request.getDelay(), request.getUnit());

				DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
					.withLocale(i18nService.getLocale());
				text = MessageFormat.format(i18nService.get("notification.shutdown"), formatter.format(start));
			}
		}

		return new NotificationData(REMOTE_PC_CONTROL, text, theme, notificationDuration, position);
	}

	private NotificationData buildCancelShutdownNotification() {
		String text = runtimeConfig.isDryRun() ? i18nService.get("notification.cancelShutdown.dryrun")
				: i18nService.get("notification.cancelShutdown");
		Theme theme = getTheme();
		int notificationDuration = configManager.current().getNotificationDuration();
		Position position = configManager.current().getNotificationPosition();

		return new NotificationData(REMOTE_PC_CONTROL, text, theme, notificationDuration, position);
	}

	record NotificationData(String title, String text, Theme theme, int duration, Position position) {

	}

}
