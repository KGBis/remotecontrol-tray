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

import io.github.kgbis.remotecontrol.tray.autostart.AutoStartController;
import io.github.kgbis.remotecontrol.tray.autostart.AutoStartControllerImpl;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigManager;
import io.github.kgbis.remotecontrol.tray.configuration.ConfigStorageImpl;
import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import io.github.kgbis.remotecontrol.tray.net.info.Device;
import io.github.kgbis.remotecontrol.tray.net.internal.InfoListener;
import io.github.kgbis.remotecontrol.tray.ui.support.DialogHandler;
import io.github.kgbis.remotecontrol.tray.ui.support.DialogHandlerImpl;
import io.github.kgbis.remotecontrol.tray.ui.support.DialogMode;
import io.github.kgbis.remotecontrol.tray.ui.support.InformationTableModelUpdater;
import io.github.kgbis.remotecontrol.tray.ui.support.Localized;
import io.github.kgbis.remotecontrol.tray.ui.support.SettingsDialogFactory;
import io.github.kgbis.remotecontrol.tray.ui.support.SettingsDialogFactoryImpl;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import javax.swing.ImageIcon;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.github.kgbis.remotecontrol.tray.ui.support.TraySupportDetector.isFullTraySupport;
import static io.github.kgbis.remotecontrol.tray.ui.support.TraySupportDetector.isPartialTraySupport;

@Singleton
@Slf4j
public class InformationScreen implements InfoListener {

	private final List<Localized> components = new ArrayList<>();

	private final JFrame frame;

	@Getter(value = AccessLevel.PROTECTED)
	private final DefaultTableModel tableModel;

	private final InformationHolder informationHolder;

	private final InformationTableModelUpdater renderer;

	private final DialogHandler dialogHandler;

	private final I18nService i18nService;

	@Inject
	public InformationScreen(DialogHandler dialogHandler, I18nService i18nService) {
		this.dialogHandler = dialogHandler;
		this.i18nService = i18nService;
		this.informationHolder = new InformationHolder();

		i18nService.addListener(this::refreshTexts);

		this.tableModel = new DefaultTableModel(new Object[] { "type", "ip", "mac" }, 0) {

			@Override
			public String getColumnName(int column) {
				return i18nService.get("mainScreen.table.column." + column);
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		this.renderer = new InformationTableModelUpdater(tableModel);
		this.frame = buildFrame();
	}

	JFrame buildFrame() {
		JFrame jFrame = CommonUI.windowJFrame(false);

		// ---------------------------------
		// Header panel (app name & version)
		// ---------------------------------
		JPanel headerPanel = buildHeaderPanel();
		jFrame.add(headerPanel, BorderLayout.NORTH);

		JTable table = new JTable(tableModel);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			protected void setValue(Object value) {
				if (value instanceof Device.InterfaceType type) {
					value = i18nService.get("mainScreen.table.content." + type.name());
				}
				super.setValue(value);
			}
		});
		components.add(Localized.tableHeaders(table, "mainScreen.table.column.", i18nService));
		components.add(Localized.tableRepaint(table));

		JScrollPane scroll = new JScrollPane(table);
		int preferredHeight = table.getRowHeight() * 5 + table.getTableHeader().getPreferredSize().height;
		scroll.setPreferredSize(new Dimension(scroll.getPreferredSize().width, preferredHeight));

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

	@Override
	public void onChange(Device device) {
		informationHolder.set(device);
		renderer.render(device.getInterfaces());
	}

	/* private methods */

	private JPanel buildHeaderPanel() {
		JPanel headerPanel = CommonUI.createHeaderPanel();

		// Line 1 - Left: Empty
		JPanel versionPanel = new JPanel(new BorderLayout());
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		versionPanel.add(leftPanel, BorderLayout.WEST);

		// Line 1 - Right: Settings button
		JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		ImageIcon settings = new ImageIcon(ResourcesHelper.getImage("settings"));
		JButton settingsButton = new JButton(settings);
		settingsButton.setToolTipText(i18nService.get("mainScreen.button.settings.tooltip"));
		settingsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		settingsButton.setBorderPainted(false);
		settingsButton.setContentAreaFilled(false);
		settingsButton.setFocusPainted(false);
		settingsButton.setMargin(new Insets(0, 0, 0, 0));
		settingsButton.setPreferredSize(new Dimension(16, 16));
		settingsButton.addActionListener(e -> dialogHandler.run(frame, DialogMode.SETTINGS));
		components.add(Localized.tooltip(settingsButton, "mainScreen.button.settings.tooltip", i18nService));
		settingsPanel.add(settingsButton);

		versionPanel.add(settingsPanel, BorderLayout.EAST);

		// Line 2: Description
		JPanel descPanel = new JPanel();
		JLabel descLabel = new JLabel(i18nService.get("mainScreen.label.detected"), SwingConstants.LEADING);
		descLabel.setFont(descLabel.getFont().deriveFont(Font.BOLD));
		components.add(Localized.text(descLabel, "mainScreen.label.detected", i18nService));
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
			JButton exitBtn = new JButton(i18nService.get("mainScreen.button.exit"));
			exitBtn.addActionListener(e -> System.exit(0));
			components.add(Localized.text(exitBtn, "mainScreen.button.exit", i18nService));
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
			JButton copyBtn = new JButton(i18nService.get("mainScreen.button.copy"));
			copyBtn.addActionListener(e -> {
				int row = table.getSelectedRow();
				copyToClipboard(row);
			});
			components.add(Localized.text(copyBtn, "mainScreen.button.copy", i18nService));
			rightPanel.add(copyBtn);
		}

		// Close button only available with full Tray support
		// Windows OK, Cinnamon OK, Mate OK, XFCE OK, KDE OK, LXQt OK, Gnome OK
		if (isFullTraySupport()) {
			JButton closeBtn = new JButton(i18nService.get("mainScreen.button.close"));
			closeBtn.addActionListener(e -> frame.setVisible(false));
			components.add(Localized.text(closeBtn, "mainScreen.button.close", i18nService));
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
		onChange(informationHolder.get());
	}

	// Copy all or selected row to clipboard as csv
	private void copyToClipboard(int row) {
		String toCopy = tableToCsv(row);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(toCopy.trim()), null);
		log.debug("Copied {} to clipboard:\n{}", row == -1 ? "all rows" : "row #" + row, toCopy);
	}

	private String tableToCsv(int rowNumber) {
		// header
		String header = IntStream.range(0, tableModel.getColumnCount())
			.mapToObj(tableModel::getColumnName)
			.collect(Collectors.joining(","));

		// common function for rows
		IntFunction<Stream<String>> streamIntFunction = row -> IntStream.range(0, tableModel.getColumnCount())
			.mapToObj(col -> {
				Object value = tableModel.getValueAt(row, col);
				return value == null ? "" : value.toString();
			});

		// rows
		String rows = (rowNumber == -1)
				? IntStream.range(0, tableModel.getRowCount())
					.mapToObj(row -> streamIntFunction.apply(row).collect(Collectors.joining(",")))
					.collect(Collectors.joining("\n"))
				: streamIntFunction.apply(rowNumber).collect(Collectors.joining(","));

		return header + "\n" + rows;
	}

	private void refreshTexts(PropertyChangeEvent evt) {
		if ("locale".equals(evt.getPropertyName())) {
			components.forEach(component -> component.applier().accept(component.value().get()));
		}
	}

	static final class InformationHolder {

		private final AtomicReference<Device> device = new AtomicReference<>();

		public synchronized Device get() {
			log.debug("Retrieving information for {}", device.get());
			if (device.get() == null) {
				log.error("Device has not been set. Null");
				device.set(Device.builder().build());
			}

			return device.get();
		}

		public synchronized void set(Device device) {
			this.device.set(device);
		}

	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			throw new RuntimeException(); // NOSONAR
		}

		ConfigManager manager = new ConfigManager(new ConfigStorageImpl());
		I18nService i18nService = new I18nService(manager);
		AutoStartController autoStartController = new AutoStartControllerImpl();
		SettingsDialogFactory factory = new SettingsDialogFactoryImpl(i18nService);
		DialogHandler handler = new DialogHandlerImpl(manager, autoStartController, factory);
		InformationScreen dialog = new InformationScreen(handler, i18nService);
		dialog.onChange(Device.builder()
			.interfaces(Set.of(Device.DeviceInterface.builder()
				.type(Device.InterfaceType.WIFI)
				.ip("192.168.1.66")
				.mac("01:23:34:56:78:9A")
				.build()))
			.build());

		dialog.show();
	}

}
