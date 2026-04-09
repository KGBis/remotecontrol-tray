package io.github.kgbis.remotecontrol.tray.ui.settings.panels;

import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.ui.CommonUI;
import io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel;
import io.github.kgbis.remotecontrol.tray.ui.support.Languages;
import lombok.extern.slf4j.Slf4j;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@Slf4j
public class LanguageSettingsPanel extends SettingsPanel {

	public LanguageSettingsPanel(SettingsModel model, I18nService i18nService, boolean markAsNew) {
		super(model, i18nService, markAsNew);
	}

	@Override
	void buildPanel(SettingsModel model, I18nService i18nService, boolean markAsNew) {
		JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		langPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel langLabel = new JLabel(i18nService.get("settingsDialog.lang.text"));
		langLabel.setFont(langLabel.getFont().deriveFont(Font.BOLD));

		JComboBox<Languages> langComboBox = new JComboBox<>();
		Arrays.stream(Languages.values()).forEach(langComboBox::addItem);
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
		langComboBox.setSelectedItem(Languages.fromLocale(model.getLanguage()));
		langComboBox.addActionListener(e -> {
			Languages selected = (Languages) langComboBox.getSelectedItem();
			Locale locale = Objects.requireNonNull(selected).getLocale();
			model.setLanguage(locale);
			i18nService.setLocale(locale);
		});

		langPanel.add(langLabel);
		langPanel.add(langComboBox);

		if (markAsNew) {
			CommonUI.markAsNewFeature(langPanel);
		}

		// Description
		int indent = langComboBox.getInsets().left + 18;

		JLabel description = new JLabel(i18nService.get("settingsDialog.lang.description"));
		description.setForeground(Color.DARK_GRAY);
		description.setAlignmentX(Component.LEFT_ALIGNMENT);
		description.setBorder(BorderFactory.createEmptyBorder(5, indent, 0, 0));

		add(langPanel);
		add(description);
		add(Box.createVerticalStrut(10));
	}

}