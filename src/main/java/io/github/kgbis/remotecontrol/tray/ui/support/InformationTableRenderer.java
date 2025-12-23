package io.github.kgbis.remotecontrol.tray.ui.support;

import javax.swing.table.DefaultTableModel;
import java.util.Map;

public final class InformationTableRenderer {

	private final DefaultTableModel model;

	public InformationTableRenderer(DefaultTableModel model) {
		this.model = model;
	}

	public void render(Map<String, String> data) {
		model.setRowCount(0);
		data.forEach((ip, mac) -> model.addRow(new Object[] { ip, mac }));
	}

}
