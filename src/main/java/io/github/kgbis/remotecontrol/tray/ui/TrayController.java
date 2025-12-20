/*
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */
package io.github.kgbis.remotecontrol.tray.ui;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class TrayController {

	private final InformationScreen informationScreen;

	@Inject
	public TrayController(InformationScreen informationScreen) {
		this.informationScreen = informationScreen;
	}

	public void toggleWindow() {
		if (informationScreen.isVisible()) {
			informationScreen.hide();
		}
		else {
			informationScreen.show();
		}
	}

}
