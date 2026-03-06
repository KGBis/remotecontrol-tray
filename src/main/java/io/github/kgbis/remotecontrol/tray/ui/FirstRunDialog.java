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

import io.github.kgbis.remotecontrol.tray.bootstrap.BootstrapAutoStart;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;

@Slf4j
public final class FirstRunDialog extends JDialog {

	@Getter
	private transient BootstrapAutoStart result;

	private JCheckBox autostartCheck;

	public FirstRunDialog(JFrame owner) {
		super(owner, String.format("Welcome to %s", REMOTE_PC_CONTROL), true); // MODAL
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		buildUI();
	}

	private void buildUI() {
		setLayout(new BorderLayout());

		add(buildHeader(), BorderLayout.NORTH);
		add(buildCenter(), BorderLayout.CENTER);
		add(buildButtons(), BorderLayout.SOUTH);

		pack();
		setResizable(false);
		setLocationRelativeTo(getOwner());
	}

	private JPanel buildHeader() {
		JPanel panel = CommonUI.createHeaderPanel();
		panel.add(CommonUI.createVersionPanel());
		return panel;
	}

	private JPanel buildCenter() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

		JLabel text1 = new JLabel(
				"<html><b>Welcome!</b><br>" + "It appears this is the first time you run the application<br>"
						+ "or an update was installed.</html>");

		JLabel text2 = new JLabel("<html>Do you want it to start automatically when you log in?</html>");

		autostartCheck = new JCheckBox("Start automatically on login");

		text1.setAlignmentX(Component.LEFT_ALIGNMENT);
		text2.setAlignmentX(Component.LEFT_ALIGNMENT);
		autostartCheck.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel.add(text1);
		panel.add(Box.createVerticalStrut(10));
		panel.add(text2);
		panel.add(Box.createVerticalStrut(10));
		panel.add(autostartCheck);

		return panel;
	}

	private JPanel buildButtons() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

		JButton accept = new JButton("Accept");

		accept.addActionListener(e -> {
			result = new BootstrapAutoStart(autostartCheck.isSelected());
			dispose();
		});

		panel.add(accept);

		return panel;
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

		FirstRunDialog dialog = new FirstRunDialog(null);
		dialog.setVisible(true);

		BootstrapAutoStart result = dialog.getResult();
		log.debug("Result is: {}", result);
		dialog.dispose();
	}

}