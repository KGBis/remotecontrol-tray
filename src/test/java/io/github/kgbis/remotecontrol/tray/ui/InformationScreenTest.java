package io.github.kgbis.remotecontrol.tray.ui;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.table.DefaultTableModel;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class InformationScreenTest {

	@InjectMocks
	InformationScreen informationScreen;

	@Test
	void onChange_updatesTableModel() {
		DefaultTableModel model = informationScreen.getModel();

		Map<String, String> data = Map.of("192.168.1.10", "00:11:22:33:44:55");
		informationScreen.onChange(data);

		assertEquals(1, model.getRowCount());
		assertEquals("192.168.1.10", model.getValueAt(0, 0));
		assertEquals("00:11:22:33:44:55", model.getValueAt(0, 1));
	}

}
