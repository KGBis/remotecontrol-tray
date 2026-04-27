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
package io.github.kgbis.remotecontrol.tray.ui.settings;

import com.google.inject.assistedinject.Assisted;
import com.jthemedetecor.OsThemeDetector;
import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigStorageImpl;
import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import io.github.kgbis.remotecontrol.tray.misc.RuntimeConfig;
import io.github.kgbis.remotecontrol.tray.ui.CommonUI;
import io.github.kgbis.remotecontrol.tray.ui.settings.panels.AutostartSettingsPanel;
import io.github.kgbis.remotecontrol.tray.ui.settings.panels.LanguageSettingsPanel;
import io.github.kgbis.remotecontrol.tray.ui.settings.panels.NotificationsSettingsPanel;
import io.github.kgbis.remotecontrol.tray.ui.support.ActionDesktopNotifier;
import io.github.kgbis.remotecontrol.tray.ui.support.DialogMode;
import jakarta.annotation.Nullable;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Locale;

import static io.github.kgbis.remotecontrol.tray.ui.CommonUI.TITLE;
import static io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel.AUTOSTART_FEATURE;
import static io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel.LANGUAGE_FEATURE;
import static io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel.NOTIFICATIONS_FEATURE;

@Slf4j
public final class SettingsDialog extends JDialog {

	private final I18nService i18nService;

	private final ActionDesktopNotifier actionDesktopNotifier;

	private final boolean isOnboarding;

	private final boolean isSettings;

	private final transient Range<Integer> range;

	@Getter
	private transient SettingsModel settingsModel;

	@Inject
	public SettingsDialog(I18nService i18nService, ConfigManager configManager,
			ActionDesktopNotifier actionDesktopNotifier, @Assisted @Nullable JFrame owner, @Assisted DialogMode mode,
			@Assisted int versionLevel) {
		super(owner,
				mode.equals(DialogMode.ONBOARDING)
						? MessageFormat.format(i18nService.get("settingsDialog.title.welcome"), TITLE)
						: MessageFormat.format(i18nService.get("settingsDialog.title.settings"), TITLE),
				true);
		this.i18nService = i18nService;
		this.actionDesktopNotifier = actionDesktopNotifier;
		this.isSettings = mode == DialogMode.SETTINGS;
		this.isOnboarding = mode == DialogMode.ONBOARDING;

		Config config = configManager.current();
		this.range = Range.of(config.getOnboardingVersion() + 1, versionLevel);
		this.settingsModel = SettingsModel.of(config);

		configureDialog();
		buildUI(owner);
	}

	private void configureDialog() {
		if (isSettings) {
			this.setModalityType(ModalityType.APPLICATION_MODAL);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}

		// Only force to click button to close when ONBOARDING
		if (isOnboarding) {
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		}

		// Window icon
		setIconImage(ResourcesHelper.getImage("computer"));

		// Configure tooltips
		configureTooltips();

		// Register exit by Escape key when in SETTINGS mode
		registerEscapeKey();
	}

	private void configureTooltips() {
		UIManager.put("ToolTip.background", new Color(60, 60, 60));
		UIManager.put("ToolTip.foreground", Color.WHITE);
		UIManager.put("ToolTip.border", BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(80, 80, 80)), BorderFactory.createEmptyBorder(6, 8, 6, 8)));

		UIManager.put("ToolTip.font", new Font("Segoe UI", Font.PLAIN, 12));
	}

	private void buildUI(JFrame owner) {
		setLayout(new BorderLayout());

		add(header(), BorderLayout.NORTH);
		add(options(), BorderLayout.CENTER);
		add(button(), BorderLayout.SOUTH);

		pack();
		setResizable(false);

		if (owner == null) {
			setLocationRelativeTo(getOwner()); // Centered on screen
		}
		else {
			int dialogWidth = getWidth();
			int dialogHeight = getHeight();
			int parentX = owner.getX();
			int parentY = owner.getY();
			int parentWidth = owner.getWidth();
			int parentHeight = owner.getHeight();

			int dialogX = parentX + (parentWidth - dialogWidth) / 2;
			int dialogY = parentY - dialogHeight + parentHeight - 1;

			setLocation(dialogX, dialogY); // adjusted to the bottom of the app's - 1px
		}
	}

	private JPanel header() {
		return CommonUI.createHeaderPanel();
	}

	private JPanel options() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

		// Main text above options
		addMainText(panel);

		// Options
		addOptions(panel);

		// lower spacing
		panel.add(Box.createVerticalStrut(10));

		return panel;
	}

	/**
	 * Text before settings' options
	 * @param panel parent panel
	 */
	private void addMainText(JPanel panel) {
		String key = isOnboarding ? "settingsDialog.mainText.welcome" : "settingsDialog.mainText.settings";
		JLabel label = new JLabel(i18nService.get(key), SwingConstants.LEADING);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel.add(label);
		panel.add(Box.createVerticalStrut(10));
	}

	/**
	 * All the options that are to be rendered in the panel, separated by a JSeparator.
	 * @param panel Parent panel
	 */
	private void addOptions(JPanel panel) {
		// Language
		LanguageSettingsPanel languagePanel = new LanguageSettingsPanel(settingsModel, i18nService,
				isNewFeature(LANGUAGE_FEATURE));
		panel.add(languagePanel);
		panel.add(new JSeparator());
		panel.add(Box.createVerticalStrut(10));

		// Application start on login
		AutostartSettingsPanel autostartPanel = new AutostartSettingsPanel(settingsModel, i18nService,
				isNewFeature(AUTOSTART_FEATURE));
		panel.add(autostartPanel);
		panel.add(new JSeparator());
		panel.add(Box.createVerticalStrut(10));

		// Shutdown and Cancel notifications
		NotificationsSettingsPanel notificationsPanel = new NotificationsSettingsPanel(i18nService,
				actionDesktopNotifier, settingsModel, isNewFeature(NOTIFICATIONS_FEATURE));
		panel.add(notificationsPanel);
	}

	private boolean isNewFeature(int wantedVersion) {
		return isOnboarding && range.contains(wantedVersion);
	}

	private JPanel button() {
		JPanel buttonPanel = new JPanel(new BorderLayout());
		if (isSettings) {
			JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			cancelPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 10));
			JButton cancelBtn = new JButton(i18nService.get("settingsDialog.button.cancel"));
			cancelBtn.addActionListener(e -> {
				settingsModel = null;
				dispose();
			});
			cancelPanel.add(cancelBtn);

			buttonPanel.add(cancelPanel, BorderLayout.WEST);
		}

		JPanel acceptPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		acceptPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 20));

		String acceptLabelKey = isOnboarding ? "settingsDialog.button.start" : "settingsDialog.button.save";

		JButton acceptBtn = new JButton(i18nService.get(acceptLabelKey));
		getRootPane().setDefaultButton(acceptBtn);
		acceptBtn.addActionListener(e -> dispose());

		acceptPanel.add(acceptBtn);
		buttonPanel.add(acceptPanel, BorderLayout.EAST);

		return buttonPanel;
	}

	private void registerEscapeKey() {
		if (isSettings) {
			getRootPane().registerKeyboardAction(e -> {
				settingsModel = null;
				dispose();
			}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		}
	}

	/**
	 * quick test of the Swing dialog
	 * @param args args
	 */
	public static void main(String[] args) {
		// To fix blurry fonts on Linux
		if (SystemUtils.IS_OS_LINUX) {
			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ignored) {
			// don't care about any exception here
		}

		// app version level from n to n+x (Start at logon)
		Config config = Config.builder().locale(Locale.of("es").stripExtensions()).build();

		ConfigManager manager = new ConfigManager(new ConfigStorageImpl());
		I18nService i18nService = new I18nService(manager);
		i18nService.setLocale(config.getLocale());
		log.debug("i18n service locale -> {}", i18nService.getLocale());
		log.debug("config locale -> {}", config.getLocale());

		RuntimeConfig runtimeConfig = new RuntimeConfig();
		runtimeConfig.setDryRun(true);

		OsThemeDetector osThemeDetector = OsThemeDetector.getDetector();

		ActionDesktopNotifier desktopNotifier = new ActionDesktopNotifier(i18nService, manager, runtimeConfig,
				osThemeDetector);

		int versionLevel = 3;

		SettingsDialog dialog = new SettingsDialog(i18nService, manager, desktopNotifier, null, DialogMode.ONBOARDING,
				versionLevel);
		dialog.setVisible(true);

		SettingsModel result = dialog.getSettingsModel();
		log.debug("Result is: {}", result);
		dialog.dispose();

		// DialogMode.SETTINGS
		config.setLocale(Locale.of("en"));
		i18nService.setLocale(config.getLocale());
		config.setAppAutoStartOnLogin(true);
		dialog = new SettingsDialog(i18nService, manager, desktopNotifier, null, DialogMode.SETTINGS, versionLevel);
		dialog.setVisible(true);

		result = dialog.getSettingsModel();
		log.debug("Result is: {}", result);
	}

}