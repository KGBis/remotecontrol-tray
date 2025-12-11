package io.github.kgbis.remotecontrol.tray.misc;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourcesHelper {

	private static Image image;

	private static String version;

	public static Image getIcon() {
		if (image == null) {
			image = Toolkit.getDefaultToolkit()
				.getImage(ResourcesHelper.class.getClassLoader().getResource("computer.png"));
		}
		return image;
	}

	public static String getVersion() {
		if (version == null) {
			try (InputStream in = ResourcesHelper.class.getClassLoader().getResourceAsStream("version.txt")) {
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
