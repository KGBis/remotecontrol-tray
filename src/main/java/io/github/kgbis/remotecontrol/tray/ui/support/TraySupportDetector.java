package io.github.kgbis.remotecontrol.tray.ui.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraySupportDetector {

	private static TraySupport traySupport;

	public static boolean isFullTraySupport() {
		return getTraySupport().equals(TraySupport.FULL);
	}

	public static boolean isPartialTraySupport() {
		return getTraySupport().equals(TraySupport.PARTIAL);
	}

	public static boolean isNoneTraySupport() {
		return getTraySupport().equals(TraySupport.NONE);
	}

	public static String getDesktop() {
		String desktop = System.getenv("XDG_CURRENT_DESKTOP");
		if (StringUtils.isBlank(desktop)) {
			desktop = System.getenv("DESKTOP_SESSION");
		}
		return StringUtils.isBlank(desktop) ? "" : desktop.toLowerCase();
	}

	private static TraySupport getTraySupport() {
		if(traySupport == null) {
			traySupport = detect();
		}

		return traySupport;
	}

	private static TraySupport detect() {
		// Windows y macOS: always reliable
		if (isWindows() || isMac()) {
			return TraySupport.FULL;
		}

		// Linux
		String desktop = getDesktop().toLowerCase();

		// --- GNOME ---
		if (desktop.contains("gnome")) {
			// Fedora GNOME: broken
			if (isFedoraGnome()) {
				return TraySupport.NONE;
			}
			// GNOME "normal": visible tray visible, events unrealiable
			return TraySupport.PARTIAL;
		}

		// --- KDE Plasma ---
		if (desktop.contains("kde") || desktop.contains("plasma")) {
			// Icono can appear, broken events
			return TraySupport.NONE;
		}

		// --- Cinnamon ---
		if (desktop.contains("cinnamon") || hasEnv("CINNAMON_VERSION")) {
			// GTK3 + AppIndicator + Cinnamon = broken events
			return TraySupport.NONE;
		}

		// --- XFCE ---
		if (desktop.contains("xfce")) {
			return TraySupport.FULL;
		}

		// --- MATE ---
		if (desktop.contains("mate")) {
			return TraySupport.FULL;
		}

		// --- LXQt / LXDE ---
		if (desktop.contains("lxqt") || desktop.contains("lxde")) {
			return TraySupport.FULL;
		}

		// Fallback to nnne
		return TraySupport.NONE;
	}

	/* ================= helpers ================= */

	private static boolean isFedoraGnome() {
		// Specific variable for Fedora GNOME (PTYXIS patches)
		return hasEnv("PTYXIS_VERSION");
	}

	private static boolean hasEnv(String name) {
		return System.getenv(name) != null;
	}

	private static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	private static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().contains("mac");
	}

}
