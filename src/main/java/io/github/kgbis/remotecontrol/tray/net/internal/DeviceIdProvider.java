package io.github.kgbis.remotecontrol.tray.net.internal;

import io.github.kgbis.remotecontrol.tray.misc.ResourcesHelper;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.util.UUID;

@Singleton
public class DeviceIdProvider {

	public UUID getDeviceId() throws IOException {
		return ResourcesHelper.getSystemId();
	}

}
