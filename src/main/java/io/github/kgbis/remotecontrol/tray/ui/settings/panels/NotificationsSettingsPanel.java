package io.github.kgbis.remotecontrol.tray.ui.settings.panels;

import dorkbox.notify.Position;
import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import io.github.kgbis.remotecontrol.tray.ui.CommonUI;
import io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel;
import io.github.kgbis.remotecontrol.tray.ui.support.ActionDesktopNotifier;
import io.github.kgbis.remotecontrol.tray.ui.support.NotificationDuration;
import lombok.extern.slf4j.Slf4j;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.text.MessageFormat;
import java.util.Arrays;

import static io.github.kgbis.remotecontrol.tray.ui.CommonUI.setEnabledComponents;

@Slf4j
public class NotificationsSettingsPanel extends SettingsPanel {

	private static final Position[][] positions = { { Position.TOP_LEFT, Position.TOP, Position.TOP_RIGHT },
			{ null, Position.CENTER, null }, { Position.BOTTOM_LEFT, Position.BOTTOM, Position.BOTTOM_RIGHT } };

	private final transient ActionDesktopNotifier actionDesktopNotifier;

	private JCheckBox notificationCheck;

	public NotificationsSettingsPanel(I18nService i18nService, ActionDesktopNotifier actionDesktopNotifier,
			SettingsModel model, boolean markAsNew) {
		super(model, i18nService, markAsNew);
		this.actionDesktopNotifier = actionDesktopNotifier;
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

		positionPanel.add(label);
		positionPanel.add(Box.createVerticalStrut(5));

		JPanel grid = new JPanel(new GridLayout(3, 3, 2, 2));
		grid.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		grid.setAlignmentX(Component.LEFT_ALIGNMENT);

		ButtonGroup group = new ButtonGroup();

		ImageIcon icon = new ImageIcon(ResourcesHelper.getImage("notification"));

		for (Position[] row : positions) {
			for (Position pos : row) {
				if (pos == null) {
					grid.add(new JLabel());
					continue;
				}

				JToggleButton btn = new JToggleButton(icon); /* "●" */
				btn.setPreferredSize(new Dimension(16, 16));
				btn.setBorder(BorderFactory.createEmptyBorder());
				btn.setFocusPainted(false);

				if (pos.equals(model.getNotificationPosition())) {
					btn.setSelected(true);
				}

				btn.addActionListener(e -> {
					model.setNotificationPosition(pos);
					actionDesktopNotifier.preview(pos, i18nService.get("settingsDialog.notify.preview"));
				});

				group.add(btn);
				grid.add(btn);
			}
		}
		positionPanel.add(grid);

		return positionPanel;
	}

}
