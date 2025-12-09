package io.github.kgbis.remotecontrol.tray.ui;

import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

@NoArgsConstructor
@Singleton
public class ResourcesHelper {

	private Image image;

	private String version;

	public Image getIcon() {
		if (image == null) {
			image = Toolkit.getDefaultToolkit()
				.getImage(this.getClass().getClassLoader().getResource("remote-control_16x16.png"));
		}
		return image;
	}

	public String getVersion() {
		if (version == null) {
			try (InputStream in = getClass().getClassLoader().getResourceAsStream("version.txt")) {
				if (in == null) {
					version = "unknown";
				}
				else {
					version = new String(in.readAllBytes()).trim();
				}
			}
			catch (IOException e) {
				version = "unknown";
			}
		}

		return version;
	}

}
