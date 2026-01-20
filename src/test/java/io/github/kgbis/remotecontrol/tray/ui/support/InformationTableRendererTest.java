package io.github.kgbis.remotecontrol.tray.ui.support;

import org.junit.jupiter.api.Test;

import javax.swing.table.DefaultTableModel;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InformationTableRendererTest {

	@Test
	void render_updatesTableModel() {
		DefaultTableModel table = new DefaultTableModel(new Object[] { "IP", "MAC" }, 0);

		InformationTableRenderer renderer = new InformationTableRenderer(table);

		renderer.render(Map.of("192.168.1.10", "00:11:22"));

		assertEquals(1, table.getRowCount());
		assertEquals("192.168.1.10", table.getValueAt(0, 0));
		assertEquals("00:11:22", table.getValueAt(0, 1));
	}

}
