package io.github.kgbis.remotecontrol.tray.ui.settings.panels;

import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.ui.CommonUI;
import io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;

public class AutostartSettingsPanel extends SettingsPanel {

	public AutostartSettingsPanel(SettingsModel model, I18nService i18nService, boolean markAsNew) {
		super(model, i18nService, markAsNew);
	}

	@Override
	void buildPanel(SettingsModel model, I18nService i18nService, boolean markAsNew) {
		JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		checkPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		String checkKey = "settingsDialog.autostart.text";
		JCheckBox autostartCheck = new JCheckBox(i18nService.get(checkKey));
		autostartCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
		autostartCheck.setFont(autostartCheck.getFont().deriveFont(Font.BOLD));
		autostartCheck.setHorizontalTextPosition(SwingConstants.LEFT);
		autostartCheck.setSelected(model.isAutostartEnabled());
		autostartCheck.addItemListener(e -> model.setAutostartEnabled(e.getStateChange() == ItemEvent.SELECTED));

		String descKey = "settingsDialog.autostart.description";
		JLabel description = new JLabel(i18nService.get(descKey));
		description.setForeground(Color.DARK_GRAY);
		description.setAlignmentX(Component.LEFT_ALIGNMENT);

		int indent = autostartCheck.getInsets().left + 18;
		description.setBorder(BorderFactory.createEmptyBorder(0, indent, 0, 0));

		checkPanel.add(autostartCheck);

		if (markAsNew) {
			CommonUI.markAsNewFeature(checkPanel);
		}

		add(checkPanel);
		add(description);
		add(Box.createVerticalStrut(10));
	}

}
