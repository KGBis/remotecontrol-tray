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
package io.github.kgbis.remotecontrol.tray.ui.settings.panels;

import dorkbox.notify.Position;
import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import io.github.kgbis.remotecontrol.tray.ui.CommonUI;
import io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel;
import io.github.kgbis.remotecontrol.tray.ui.support.ActionDesktopNotifier;
import io.github.kgbis.remotecontrol.tray.ui.support.HighlightType;
import io.github.kgbis.remotecontrol.tray.ui.support.NotificationDuration;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import static io.github.kgbis.remotecontrol.tray.ui.CommonUI.setEnabledComponents;

@Slf4j
public class NotificationsSettingsPanel extends SettingsPanel {

	private final transient ActionDesktopNotifier actionDesktopNotifier;

	Position hoveredZone = null;

	Position selectedZone;

	private transient Map<Position, Rectangle> zones;

	private JCheckBox notificationCheck;

	public NotificationsSettingsPanel(I18nService i18nService, ActionDesktopNotifier actionDesktopNotifier,
			SettingsModel model, boolean markAsNew) {
		super(model, i18nService, markAsNew);
		this.actionDesktopNotifier = actionDesktopNotifier;
		this.selectedZone = model.getNotificationPosition();
	}

	@Override
	void buildPanel(SettingsModel model, I18nService i18nService, boolean markAsNew) {
		JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		checkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Main checkbox, description and Windows OS note
		String checkKey = "settingsDialog.notify.text";
		notificationCheck = new JCheckBox(i18nService.get(checkKey));
		notificationCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
		notificationCheck.setFont(notificationCheck.getFont().deriveFont(Font.BOLD));
		notificationCheck.setHorizontalTextPosition(SwingConstants.LEFT);
		notificationCheck.setSelected(model.isNotificationsEnabled());

		int indent = notificationCheck.getInsets().left + 18;

		// Setting description
		String descKey = "settingsDialog.notify.description";
		JLabel description = new JLabel(i18nService.get(descKey));
		description.setForeground(Color.DARK_GRAY);
		description.setAlignmentX(Component.LEFT_ALIGNMENT);
		description.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));

		// Setting note for Windows
		String noteKey = "settingsDialog.notify.note";
		JLabel note = new JLabel(i18nService.get(noteKey));
		note.setFont(notificationCheck.getFont().deriveFont(Font.ITALIC));
		note.setForeground(Color.DARK_GRAY);
		note.setAlignmentX(Component.LEFT_ALIGNMENT);
		note.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));

		checkPanel.add(notificationCheck);

		if (markAsNew) {
			CommonUI.markAsNewFeature(checkPanel);
		}

		add(checkPanel);
		add(description);
		add(note);
		add(Box.createVerticalStrut(10));
		add(buildNotificationDurationPositionPanels(model, i18nService));
	}

	private JPanel buildNotificationDurationPositionPanels(SettingsModel model, I18nService i18nService) {
		JPanel durationAndPositionPanel = new JPanel(new GridLayout(1, 2, 0, 0));
		durationAndPositionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel durationPanel = buildDurationPanel(model, i18nService);
		durationPanel.setBorder(BorderFactory.createEmptyBorder(0, getInsets().left + 18, 0, 0));

		JPanel positionPanel = buildPositionPanel(model, i18nService);

		// add both subpanels left and right
		durationAndPositionPanel.add(durationPanel);
		durationAndPositionPanel.add(positionPanel);

		// Add listener to change and update with current values
		notificationCheck.addItemListener(e -> {
			boolean enabled = e.getStateChange() == ItemEvent.SELECTED;
			model.setNotificationsEnabled(enabled);
			setEnabledComponents(enabled, durationPanel, positionPanel);
		});
		setEnabledComponents(notificationCheck.isSelected(), durationPanel, positionPanel);

		return durationAndPositionPanel;
	}

	private JPanel buildDurationPanel(SettingsModel model, I18nService i18nService) {
		JPanel lifespanPanel = new JPanel();
		lifespanPanel.setLayout(new BoxLayout(lifespanPanel, BoxLayout.Y_AXIS));

		// Display time label
		JLabel jLabel = new JLabel(i18nService.get("settingsDialog.notify.ttl"));
		jLabel.setFont(jLabel.getFont().deriveFont(Font.BOLD));
		lifespanPanel.add(jLabel);

		ButtonGroup ttlButtonGroup = new ButtonGroup();
		Arrays.stream(NotificationDuration.values()).forEach(duration -> {
			int ttl = duration.getTtl() == 0 ? 0 : duration.getTtl() / 1000;
			String text = MessageFormat.format(i18nService.get(duration.getTextKey()), ttl);
			JRadioButton rb = new JRadioButton(text);
			if (duration.getTtl() == model.getNotificationsDuration()) {
				rb.setSelected(true);
			}
			rb.addActionListener(e -> {
				model.setNotificationsDuration(duration.getTtl());
				log.debug("model = {}", model);
			});
			ttlButtonGroup.add(rb);
			lifespanPanel.add(rb);
		});

		return lifespanPanel;
	}

	private JPanel buildPositionPanel(SettingsModel model, I18nService i18nService) {
		JPanel positionPanel = new JPanel();
		positionPanel.setLayout(new BoxLayout(positionPanel, BoxLayout.Y_AXIS));

		JLabel label = new JLabel(i18nService.get("settingsDialog.notify.position"));
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		label.setToolTipText(i18nService.get("settingsDialog.notify.position.tooltip"));

		positionPanel.add(label);
		positionPanel.add(Box.createVerticalStrut(5));

		JPanel panel = buildMonitorImagePanel(model, i18nService);
		positionPanel.add(panel);

		return positionPanel;
	}

	private @NonNull JPanel buildMonitorImagePanel(SettingsModel model, I18nService i18nService) {
		Image monitorImage = ResourcesHelper.getImage("monitor");
		Image disabledImage = GrayFilter.createDisabledImage(monitorImage);

		buildZones(monitorImage);

		JPanel imagePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				Coord coord = getCoordinates(this, monitorImage, 4);

				// Monitor image
				drawMonitorImage(g, coord);

				// rectangles in image's "desktop"
				int xx = coord.x() + 5; // + 5 horizontal border
				int yy = coord.y() + 5; // + 5 vertical border

				drawRect((Graphics2D) g, new Coord(xx, yy));
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(monitorImage.getWidth(null), monitorImage.getHeight(null));
			}

			private void drawMonitorImage(Graphics g, Coord coord) {
				if (isEnabled()) {
					g.drawImage(monitorImage, coord.x(), coord.y(), this);
				}
				else {
					g.drawImage(disabledImage, coord.x(), coord.y(), this);
				}
			}

			private void drawRect(Graphics2D g, Coord coord) {
				if (!isEnabled()) {
					drawSelectedArea(g, coord.x(), coord.y(), false);
					return;
				}

				if (hoveredZone != null) {
					this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					drawHoveredArea(g, coord.x(), coord.y());
				}
				else {
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

				if (selectedZone != null) {
					drawSelectedArea(g, coord.x(), coord.y(), true);
				}
			}
		};

		Coord coord = getCoordinates(imagePanel, monitorImage, 6);

		imagePanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				onMouseMoved(e, imagePanel, coord);
			}
		});

		imagePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				onMousePressed(e, imagePanel, coord, model, i18nService);
			}
		});
		return imagePanel;
	}

	private void onMousePressed(MouseEvent e, JPanel imagePanel, Coord coord, SettingsModel model,
			I18nService i18nService) {
		if (!imagePanel.isEnabled()) {
			return;
		}

		Position clicked = getZoneAt(e.getX() + coord.x(), e.getY() + coord.y());

		if (clicked != null) {
			selectedZone = clicked;
			imagePanel.repaint();
			model.setNotificationPosition(selectedZone);
			actionDesktopNotifier.preview(selectedZone, model.getNotificationsDuration(),
					i18nService.get("settingsDialog.notify.preview"));
		}
	}

	private void onMouseMoved(MouseEvent e, JPanel imagePanel, Coord coord) {
		if (!imagePanel.isEnabled()) {
			return;
		}

		Position newHover = getZoneAt(e.getX() + coord.x(), e.getY() + coord.y());

		if (newHover != hoveredZone) {
			hoveredZone = newHover;
			imagePanel.repaint();
		}
	}

	private void drawHoveredArea(Graphics2D g2, int xx, int yy) {
		Rectangle r = zones.get(hoveredZone);
		paintRectangle(g2, xx, yy, r, HighlightType.HOVER);
	}

	private void drawSelectedArea(Graphics2D g2, int xx, int yy, boolean enabled) {
		Rectangle r = zones.get(selectedZone);
		paintRectangle(g2, xx, yy, r, enabled ? HighlightType.SELECTED : HighlightType.DISABLED);
	}

	private void paintRectangle(Graphics2D g2, int xx, int yy, Rectangle r, HighlightType color) {
		g2.setColor(color.getLight());
		g2.fillRoundRect(r.x + xx, r.y + yy, r.width, r.height, 12, 12);

		float[] dash = { 5f, 5f };
		g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dash, 0f));

		g2.setColor(color.getDark());
		g2.drawRoundRect(r.x + xx, r.y + yy, r.width, r.height, 12, 12);
	}

	private Coord getCoordinates(JPanel jPanel, Image monitorImage, int divident) {
		int panelWidth = jPanel.getWidth();
		int panelHeight = jPanel.getHeight();

		int imgWidth = monitorImage.getWidth(null);
		int imgHeight = monitorImage.getHeight(null);

		int x = (panelWidth - imgWidth) / 4;
		int y = (panelHeight - imgHeight) / divident;

		return new Coord(x, y);
	}

	private Position getZoneAt(int x, int y) {
		for (Map.Entry<Position, Rectangle> entry : zones.entrySet()) {
			if (entry.getValue().contains(x, y)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private void buildZones(Image monitorImage) {
		Image image = new ImageIcon(monitorImage).getImage();

		int w = image.getWidth(null) - 10; // monitor hor. borders
		int h = image.getHeight(null) - 30; // monitor base height

		int cellW = w / 3;
		int cellH = h / 3;

		zones = new EnumMap<>(Position.class);
		zones.put(Position.TOP_LEFT, new Rectangle(0, 0, cellW, cellH));
		zones.put(Position.TOP, new Rectangle(cellW, 0, cellW, cellH));
		zones.put(Position.TOP_RIGHT, new Rectangle(cellW * 2, 0, cellW, cellH));
		zones.put(Position.CENTER, new Rectangle(cellW, cellH, cellW, cellH));
		zones.put(Position.BOTTOM_LEFT, new Rectangle(0, cellH * 2, cellW, cellH));
		zones.put(Position.BOTTOM, new Rectangle(cellW, cellH * 2, cellW, cellH));
		zones.put(Position.BOTTOM_RIGHT, new Rectangle(cellW * 2, cellH * 2, cellW, cellH));
	}

	private record Coord(int x, int y) {
	}

}
