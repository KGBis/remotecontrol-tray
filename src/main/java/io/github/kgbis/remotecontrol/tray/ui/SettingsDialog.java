/*
 * Copyright (c) Enrique García
 *
 * This file is part of RemoteControlTray.
 *
 * RemoteControlTray is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RemoteControlTray is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RemoteControlTray.  If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.ui;

import io.github.kgbis.remotecontrol.tray.configuration.Config;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigStorageImpl;
import io.github.kgbis.remotecontrol.tray.configuration.Settings;
import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import io.github.kgbis.remotecontrol.tray.ui.support.DialogMode;
import io.github.kgbis.remotecontrol.tray.ui.support.Languages;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import static io.github.kgbis.remotecontrol.tray.ui.CommonUI.TITLE;

@Slf4j
public final class SettingsDialog extends JDialog {

	private final transient Config config;

	private final transient I18nService i18nService;

	private final boolean isOnboarding;

	private final boolean isSettings;

	private final transient Range<Integer> range;

	@Getter
	private transient Settings settings;

	/* Fields from which to take settings values */
	private JCheckBox autostartCheck;

	private JComboBox<Languages> langComboBox;

	public SettingsDialog(JFrame owner, DialogMode mode, Config config, int versionLevel, I18nService i18nService) {
		super(owner,
				mode.equals(DialogMode.ONBOARDING)
						? MessageFormat.format(i18nService.get("settingsDialog.title.welcome"), TITLE)
						: MessageFormat.format(i18nService.get("settingsDialog.title.settings"), TITLE),
				true);

		this.config = config;
		this.i18nService = i18nService;

		this.isSettings = mode == DialogMode.SETTINGS;
		this.isOnboarding = mode == DialogMode.ONBOARDING;

		this.range = Range.of(config.getOnboardingVersion() + 1, versionLevel);

		configureDialog();

		buildUI();
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

		// Register exit by Escape key when in SETTINGS mode
		registerEscapeKey();
	}

	private void buildUI() {
		setLayout(new BorderLayout());

		add(header(), BorderLayout.NORTH);
		add(options(), BorderLayout.CENTER);
		add(button(), BorderLayout.SOUTH);

		pack();
		setResizable(true);
		setLocationRelativeTo(getOwner());
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
	 * All the options that are to be rendered in the panel, separated by a JSeparator.
	 * <p/>
	 * <b>NOTE:</b> If more settings are added in the future, consider extracting each
	 * option into its own component (SettingsItem pattern).
	 * @param panel Parent panel
	 */
	private void addOptions(JPanel panel) {
		addLanguagePanel(panel); // Language
		panel.add(new JSeparator());
		addAutoStartCheckbox(panel); // Application start on login
	}

	private void addMainText(JPanel panel) {
		String key = isOnboarding ? "settingsDialog.mainText.welcome" : "settingsDialog.mainText.settings";
		JLabel label = new JLabel(i18nService.get(key), SwingConstants.LEADING);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel.add(label);
		panel.add(Box.createVerticalStrut(10));
	}

	private void addLanguagePanel(JPanel panel) {
		JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		langPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		String key = "settingsDialog.lang.text";
		JLabel langLabel = new JLabel(i18nService.get(key));
		langLabel.setFont(langLabel.getFont().deriveFont(Font.BOLD));

		langComboBox = new JComboBox<>();
		Arrays.stream(Languages.values()).forEach(langComboBox::addItem);
		langComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		langComboBox.setMaximumSize(langComboBox.getPreferredSize());
		langComboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				if (value instanceof Languages lang) {
					setText(lang.getText());
				}
				else {
					setText("");
				}

				return this;
			}
		});
		langComboBox.setSelectedItem(Languages.fromLocale(config.getLocale()));
		langComboBox.addActionListener(e -> {
			log.debug("Language selected: {}", StringUtils
				.capitalize(Objects.requireNonNull(langComboBox.getSelectedItem()).toString().toLowerCase()));
			i18nService.setLocale(((Languages) Objects.requireNonNull(langComboBox.getSelectedItem())).getLocale());
		});

		langPanel.add(langLabel);
		langPanel.add(langComboBox);

		markIfNewFeature(langPanel, 2);

		String descKey = "settingsDialog.lang.description";
		JLabel description = new JLabel(i18nService.get(descKey));
		description.setForeground(Color.DARK_GRAY);
		description.setAlignmentX(Component.LEFT_ALIGNMENT);

		int indent = langComboBox.getInsets().left + 18;
		description.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));

		panel.add(langPanel);
		panel.add(description);
		panel.add(Box.createVerticalStrut(10));
	}

	/**
	 * compose the "Autostart" checkbox.
	 * @param panel parent
	 */
	private void addAutoStartCheckbox(JPanel panel) {
		JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		checkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		String checkKey = "settingsDialog.autostart.text";
		autostartCheck = new JCheckBox(i18nService.get(checkKey));
		autostartCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
		autostartCheck.setFont(autostartCheck.getFont().deriveFont(Font.BOLD));
		autostartCheck.setHorizontalTextPosition(SwingConstants.LEFT);
		autostartCheck.setSelected(config.isAppAutoStartOnLogin());

		String descKey = "settingsDialog.autostart.description";
		JLabel description = new JLabel(i18nService.get(descKey));
		description.setForeground(Color.DARK_GRAY);
		description.setAlignmentX(Component.LEFT_ALIGNMENT);

		int indent = autostartCheck.getInsets().left + 18;
		description.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));

		checkPanel.add(autostartCheck);
		markIfNewFeature(checkPanel, 1);
		panel.add(checkPanel);
		panel.add(description);

	}

	private JPanel button() {
		JPanel buttonPanel = new JPanel(new BorderLayout());
		if (isSettings) {
			JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			cancelPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 10));
			JButton cancelBtn = new JButton(i18nService.get("settingsDialog.button.cancel"));
			cancelBtn.addActionListener(e -> {
				settings = null;
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
		acceptBtn.addActionListener(e -> {
			boolean autoStart = autostartCheck != null && autostartCheck.isSelected();
			Locale locale = ((Languages) Objects.requireNonNull(langComboBox.getSelectedItem())).getLocale();
			settings = new Settings(autoStart, locale);
			dispose();
		});

		acceptPanel.add(acceptBtn);
		buttonPanel.add(acceptPanel, BorderLayout.EAST);

		return buttonPanel;
	}

	private void registerEscapeKey() {
		if (isSettings) {
			getRootPane().registerKeyboardAction(e -> {
				settings = null;
				dispose();
			}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
		}
	}

	private void markIfNewFeature(JPanel panel, int wantedVersion) {
		// if new feature
		if (isOnboarding && range.contains(wantedVersion)) {
			JLabel newFeature = new JLabel(new ImageIcon(ResourcesHelper.getImage("new")));
			newFeature.setSize(new Dimension(16, 16));
			newFeature.setAlignmentX(Component.LEFT_ALIGNMENT);
			panel.add(newFeature);
		}
	}

	/**
	 * quick test of the Swing dialog
	 * @param args args
	 */
	public static void main(String[] args) {
		// To fix blurry fonts on Linux
		if (SystemUtils.IS_OS_UNIX) {
			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ignored) {
			// don't care about any exception here
		}

		// app version level from 0 to 1 (Start at logon)
		Config config = Config.builder().locale(Locale.of("es").stripExtensions()).build();

		ConfigManager manager = new ConfigManager(new ConfigStorageImpl());
		I18nService i18nService = new I18nService(manager);
		i18nService.setLocale(config.getLocale());
		log.debug("i18n service locale -> {}", i18nService.getLocale());
		log.debug("config locale -> {}", config.getLocale());

		int versionLevel = 2;

		SettingsDialog dialog = new SettingsDialog(null, DialogMode.ONBOARDING, config, versionLevel, i18nService);
		dialog.setVisible(true);

		Settings result = dialog.getSettings();
		log.debug("Result is: {}", result);
		dialog.dispose();

		// DialogMode.SETTINGS
		config.setAppAutoStartOnLogin(true);
		dialog = new SettingsDialog(null, DialogMode.SETTINGS, config, versionLevel, i18nService);
		dialog.setVisible(true);

		result = dialog.getSettings();
		log.debug("Result is: {}", result);
	}

}