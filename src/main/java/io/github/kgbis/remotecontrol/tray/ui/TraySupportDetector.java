package io.github.kgbis.remotecontrol.tray.ui;

public final class TraySupportDetector {

	public enum TraySupport {

		FULL, // Tray + reliable events
		PARTIAL, // Visible Tray, partial/broken events
		NONE // No tray

	}

	private TraySupportDetector() {
	}

	public static TraySupport detect() {
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
			// Icono can appear, events dead
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

		// Fallback conservador
		return TraySupport.NONE;
	}

	public static String getDesktop() {
		String desktop = System.getenv("XDG_CURRENT_DESKTOP");
		if (desktop == null || desktop.isBlank()) {
			desktop = System.getenv("DESKTOP_SESSION");
		}
		return desktop == null ? "" : desktop;
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
