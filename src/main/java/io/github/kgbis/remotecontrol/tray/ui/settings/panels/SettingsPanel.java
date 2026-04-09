package io.github.kgbis.remotecontrol.tray.ui.settings.panels;

import io.github.kgbis.remotecontrol.tray.i18n.I18nService;
import io.github.kgbis.remotecontrol.tray.ui.settings.SettingsModel;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import java.awt.Component;

public abstract class SettingsPanel extends JPanel {

	SettingsPanel(SettingsModel model, I18nService i18nService, boolean markAsNew) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);

		buildPanel(model, i18nService, markAsNew);
	}

	abstract void buildPanel(SettingsModel model, I18nService i18nService, boolean markAsNew);

}
