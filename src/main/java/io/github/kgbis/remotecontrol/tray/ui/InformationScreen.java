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
import io.github.kgbis.remotecontrol.tray.net.internal.InfoListener;
import io.github.kgbis.remotecontrol.tray.ui.support.InformationModel;
import io.github.kgbis.remotecontrol.tray.ui.support.InformationTableRenderer;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.REMOTE_PC_CONTROL;
import static io.github.kgbis.remotecontrol.tray.ui.support.TraySupportDetector.isFullTraySupport;
import static io.github.kgbis.remotecontrol.tray.ui.support.TraySupportDetector.isPartialTraySupport;

@Singleton
@Slf4j
public class InformationScreen implements InfoListener<String, String> {

	private final JFrame frame;

	@Getter(value = AccessLevel.PROTECTED)
	private final DefaultTableModel model;

	private final InformationModel infoModel;

	private final InformationTableRenderer renderer;

	public InformationScreen() {
		this.infoModel = new InformationModel();

		this.model = new DefaultTableModel(new Object[] { "IP Address", "MAC" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		this.renderer = new InformationTableRenderer(model);

		this.frame = buildFrame();
	}

	private JFrame buildFrame() {
		JFrame jFrame = new JFrame(REMOTE_PC_CONTROL);
		jFrame.setIconImage(ResourcesHelper.getIcon());
		jFrame.setLayout(new BorderLayout(10, 10));
		jFrame.setAlwaysOnTop(false);
		jFrame.getRootPane().setBorder(new EmptyBorder(10, 10, 0, 10));
		jFrame.setExtendedState(Frame.NORMAL);

		// ---------------------
		// Header panel (text)
		// ---------------------
		JPanel headerPanel = buildHeaderPanel();
		jFrame.add(headerPanel, BorderLayout.NORTH);

		JTable table = new JTable(model);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);

		JScrollPane scroll = new JScrollPane(table);
		jFrame.add(scroll, BorderLayout.CENTER);

		// -----------
		// Bottom bar
		// -----------
		JPanel buttonBar = buildBottomBar(table);
		jFrame.add(buttonBar, BorderLayout.SOUTH);

		jFrame.setMinimumSize(new Dimension(380, 200));
		jFrame.pack();
		jFrame.setVisible(false);

		// Register ESC key to close
		jFrame.getRootPane()
			.registerKeyboardAction(e -> jFrame.setVisible(false), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
					JComponent.WHEN_IN_FOCUSED_WINDOW);

		// Register CLOSE (x) window to exit
		jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		jFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				log.debug("Window close clicked. Exiting");
				System.exit(0);
			}
		});

		return jFrame;
	}

	public void show() {
		EventQueue.invokeLater(() -> {
			loadData();
			frame.pack();
			frame.setResizable(false);
			placeNearTray(frame);
			frame.setVisible(true);
			frame.toFront();
		});
	}

	public boolean isVisible() {
		return frame.isVisible();
	}

	public void hide() {
		EventQueue.invokeLater(() -> frame.setVisible(false));
	}

	/* private methods */

	private JPanel buildHeaderPanel() {
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		headerPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // margen abajo

		// Line 1: Title
		JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel versionStr = new JLabel(REMOTE_PC_CONTROL + " - Version:", SwingConstants.LEADING);
		JLabel versionVal = new JLabel(ResourcesHelper.getVersion(), SwingConstants.LEADING);
		versionVal.setFont(versionVal.getFont().deriveFont(Font.BOLD));
		versionPanel.add(versionStr);
		versionPanel.add(versionVal);

		// Line 2: Description
		JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel descLabel = new JLabel("Detected local IP and MAC addresses", SwingConstants.LEADING);
		descPanel.add(descLabel);

		// Add to panel
		headerPanel.add(versionPanel);
		headerPanel.add(descPanel);
		return headerPanel;
	}

	private JPanel buildBottomBar(JTable table) {
		JPanel buttonBar = new JPanel(new BorderLayout());

		// Left panel with "exit" button
		JPanel leftPanel = new JPanel();

		// do not show exit button with Partial Support
		if (!isPartialTraySupport()) {
			JButton exitBtn = new JButton("Exit Program");
			exitBtn.addActionListener(e -> System.exit(0));
			leftPanel.add(exitBtn);
		}

		// Right panel with "copy" and "close" buttons
		JPanel rightPanel = buildBottomRightPanel(table);

		// add left and right panel to button bar
		buttonBar.add(leftPanel, BorderLayout.WEST);
		buttonBar.add(rightPanel, BorderLayout.EAST);
		return buttonBar;
	}

	private @NonNull JPanel buildBottomRightPanel(JTable table) {
		JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		// Copy only if not partial tray support
		if (!isPartialTraySupport()) {
			JButton copyBtn = new JButton("Copy All/Selected");
			copyBtn.addActionListener(e -> {
				int row = table.getSelectedRow();
				if (row >= 0) {
					copyRow(row, table);
				}
				else {
					copyAll();
				}
			});
			rightPanel.add(copyBtn);
		}

		// Close button only available with full Tray support
		// Windows OK, Cinnamon OK, Mate OK, XFCE OK, KDE OK, LXQt OK, Gnome OK
		if (isFullTraySupport()) {
			JButton closeBtn = new JButton("Close this window");
			closeBtn.addActionListener(e -> frame.setVisible(false));
			rightPanel.add(closeBtn);
		}
		return rightPanel;
	}

	private void placeNearTray(JFrame frame) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		Rectangle screen = gd.getDefaultConfiguration().getBounds();

		int margin = 40;
		int x = screen.x + screen.width - frame.getWidth() - margin;
		int y = screen.y + screen.height - frame.getHeight() - margin;

		frame.setLocation(x, y);
	}

	// Load IPs and MACs to table
	private void loadData() {
		onChange(infoModel.getAddresses());
	}

	// Copy all to clipboard
	private void copyAll() {
		String toCopy = infoModel.getAddresses()
			.entrySet()
			.stream()
			.map(e -> e.getKey() + " -> " + e.getValue())
			.collect(Collectors.joining("\n"));

		StringSelection selection = new StringSelection(toCopy);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
		log.debug("Copied all to clipboard:\n{}", toCopy);
	}

	private void copyRow(int row, JTable table) {
		StringBuilder sb = new StringBuilder();
		for (int col = 0; col < table.getColumnCount(); col++) {
			sb.append(table.getValueAt(row, col)).append('\t');
		}
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString().trim()), null);
		log.debug("Copied selected row to clipboard:\n{}", sb);
	}

	@Override
	public void onChange(Map<String, String> map) {
		infoModel.update(map);
		renderer.render(infoModel.getAddresses());
	}

}
