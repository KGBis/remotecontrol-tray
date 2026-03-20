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
import io.github.kgbis.remotecontrol.tray.configuration.Settings;
import io.github.kgbis.remotecontrol.tray.ui.support.DialogMode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

import static io.github.kgbis.remotecontrol.tray.ui.CommonUI.TITLE;

@Slf4j
public final class SettingsDialog extends JDialog {

	private static final String TITLE_WELCOME = "Welcome to " + TITLE;

	private static final String TITLE_SETTINGS = TITLE + " - Settings";

	private static final String WELCOME_TEXT = """
			<html>
			It appears this is the first time you run the application or an update was installed.
			<br>
			Please check the option(s) below and press 'Accept' button to continue.
			<p/>
			<hr>
			</html>
			""";

	private static final String SETTINGS_TEXT = "<html>Settings:</html>";

	private final transient Config config;

	private final transient int appVersionLevel;

	private final transient DialogMode dialogMode;

	@Getter
	private transient Settings settings;

	private JCheckBox autostartCheck;

	public SettingsDialog(JFrame owner, DialogMode mode, Config config, int appVersionLevel) {
		super(owner, mode.equals(DialogMode.ONBOARDING) ? TITLE_WELCOME : TITLE_SETTINGS, true);

		this.config = config;
		this.appVersionLevel = appVersionLevel;
		this.dialogMode = mode;

		if (mode.equals(DialogMode.SETTINGS)) {
			this.setModalityType(ModalityType.APPLICATION_MODAL);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}

		// Only force to click button to close when ONBOARDING
		if (mode.equals(DialogMode.ONBOARDING)) {
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		}

		// Register exit by Escape key when in SETTINGS mode
		registerEscapeKey();

		buildUI();
	}

	private void buildUI() {
		setLayout(new BorderLayout());

		add(header(), BorderLayout.NORTH);
		add(options(), BorderLayout.CENTER);
		add(button(), BorderLayout.SOUTH);

		pack();
		setResizable(false);
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

		// Application start on login
		addAutoStartCheckbox(panel);

		// lower spacing
		panel.add(Box.createVerticalStrut(10));

		return panel;
	}

	private JPanel button() {
		JPanel buttonPanel = new JPanel(new BorderLayout());
		if (dialogMode == DialogMode.SETTINGS) {
			JPanel cancelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			cancelPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 10));
			JButton cancelBtn = new JButton("Cancel");
			cancelBtn.addActionListener(e -> {
				settings = null;
				dispose();
			});
			cancelPanel.add(cancelBtn);
			buttonPanel.add(cancelPanel, BorderLayout.WEST);
		}

		JPanel acceptPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		acceptPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 20));

		String label = dialogMode == DialogMode.ONBOARDING ? "Start" : "Save";

		JButton acceptBtn = new JButton(label);
		getRootPane().setDefaultButton(acceptBtn);
		acceptBtn.addActionListener(e -> {
			settings = new Settings(autostartCheck.isSelected());
			dispose();
		});

		acceptPanel.add(acceptBtn);
		buttonPanel.add(acceptPanel, BorderLayout.EAST);

		return buttonPanel;
	}

	private void addMainText(JPanel panel) {
		String text = dialogMode.equals(DialogMode.ONBOARDING) ? WELCOME_TEXT : SETTINGS_TEXT;
		JLabel label = new JLabel(text, SwingConstants.LEADING);
		label.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel.add(label);
		panel.add(Box.createVerticalStrut(10));
	}

	/**
	 * compose the "Autostart" checkbox if {@link DialogMode#SETTINGS} or
	 * {@link DialogMode#ONBOARDING} and app level 1 or higher
	 * @param panel parent
	 */
	private void addAutoStartCheckbox(JPanel panel) {
		boolean compose = dialogMode.equals(DialogMode.SETTINGS)
				|| (dialogMode.equals(DialogMode.ONBOARDING) && appVersionLevel < 2);

		if (compose) {
			autostartCheck = new JCheckBox("Enable Autostart feature");
			autostartCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
			autostartCheck.setFont(autostartCheck.getFont().deriveFont(Font.BOLD));
			autostartCheck.setSelected(config.isAppAutoStartOnLogin());

			JLabel description = new JLabel("Start the application automatically when you log in");
			description.setForeground(Color.DARK_GRAY);
			description.setAlignmentX(Component.LEFT_ALIGNMENT);

			int indent = autostartCheck.getInsets().left + 18;
			description.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));

			panel.add(autostartCheck);
			panel.add(description);
		}
	}

	private void registerEscapeKey() {
		if (dialogMode == DialogMode.SETTINGS) {
			getRootPane().registerKeyboardAction(e -> {
				settings = null;
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
		Config config = Config.builder().build();

		SettingsDialog dialog = new SettingsDialog(null, DialogMode.ONBOARDING, config, 1);
		dialog.setVisible(true);

		Settings result = dialog.getSettings();
		log.debug("Result is: {}", result);
		dialog.dispose();

		// DialogMode.SETTINGS
		config.setAppAutoStartOnLogin(true);
		dialog = new SettingsDialog(null, DialogMode.SETTINGS, config, -1);
		dialog.setVisible(true);

		result = dialog.getSettings();
		log.debug("Result is: {}", result);
	}

}