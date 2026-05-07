package io.github.kgbis.remotecontrol.tray.autostart;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static io.github.kgbis.remotecontrol.tray.RemoteControl.APP_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WindowsRegistryOps {

	private static final String RUN_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";

	public static boolean exists() {
		return Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, RUN_KEY, APP_NAME);
	}

	public static void set(String executablePath) {
		Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, RUN_KEY, APP_NAME, executablePath);
	}

	public static String get() {
		return Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, RUN_KEY, APP_NAME);
	}

	public static void delete() {
		if (exists()) {
			Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, RUN_KEY, APP_NAME);
		}
	}

}
