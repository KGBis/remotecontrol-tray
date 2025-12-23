package io.github.kgbis.remotecontrol.tray.ui.support;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InformationModelTest {

	@Test
	void update_replacesData() {
		InformationModel model = new InformationModel();

		model.update(Map.of("1.1.1.1", "AA"));
		model.update(Map.of("2.2.2.2", "BB"));

		assertEquals(1, model.size());
		assertTrue(model.getAddresses().containsKey("2.2.2.2"));
	}

}
