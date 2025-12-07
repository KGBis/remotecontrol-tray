package io.github.kgbis.remotecontrol.tray.ui;

import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;

import java.awt.*;

@NoArgsConstructor
@Singleton
public class IconImage {

	private Image image;

	public Image getIcon() {
		if (image == null) {
			image = Toolkit.getDefaultToolkit()
				.getImage(this.getClass().getClassLoader().getResource("remote-control_16x16.png"));
		}
		return image;
	}

}
