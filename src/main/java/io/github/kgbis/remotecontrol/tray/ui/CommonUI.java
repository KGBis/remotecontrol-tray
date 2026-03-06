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

import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUI {

	public static JFrame windowJFrame(boolean alwaysOnTop) {
		JFrame jFrame = new JFrame(REMOTE_PC_CONTROL);
		jFrame.setIconImage(ResourcesHelper.getIcon());
		jFrame.setLayout(new BorderLayout(10, 10));
		jFrame.setAlwaysOnTop(alwaysOnTop);
		jFrame.getRootPane().setBorder(new EmptyBorder(10, 10, 0, 10));
		jFrame.setExtendedState(Frame.NORMAL);

		return jFrame;
	}

	public static JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		headerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		return headerPanel;
	}

	public static JPanel createVersionPanel() {
		JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel versionStr = new JLabel(REMOTE_PC_CONTROL + " - Version:", SwingConstants.LEADING);
		JLabel versionVal = new JLabel(ResourcesHelper.getVersion(), SwingConstants.LEADING);
		versionVal.setFont(versionVal.getFont().deriveFont(Font.BOLD));
		versionPanel.add(versionStr);
		versionPanel.add(versionVal);

		return versionPanel;
	}

}
