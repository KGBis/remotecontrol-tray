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
package io.github.kgbis.remotecontrol.tray.ui;

import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonUI {

	public static final String TITLE = REMOTE_PC_CONTROL + " v" + ResourcesHelper.getVersion();

	public static JFrame windowJFrame(boolean alwaysOnTop) {
		JFrame jFrame = new JFrame(TITLE);
		jFrame.setIconImage(ResourcesHelper.getImage("computer"));
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

	public static void markAsNewFeature(JPanel panel) {
		JLabel newFeature = new JLabel(new ImageIcon(ResourcesHelper.getImage("new")));
		newFeature.setSize(new Dimension(16, 16));
		newFeature.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(newFeature);
	}

	public static void setEnabledComponents(boolean enabled, JPanel... panels) {
		for (JPanel panel : panels) {
			setEnabledRecursively(panel, enabled);
		}
	}

	private static void setEnabledRecursively(Component component, boolean enabled) {
		component.setEnabled(enabled);

		if (component instanceof Container container) {
			for (Component child : container.getComponents()) {
				setEnabledRecursively(child, enabled);
			}
		}
	}

}
