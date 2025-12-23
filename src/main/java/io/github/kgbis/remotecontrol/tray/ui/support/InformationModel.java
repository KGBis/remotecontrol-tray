package io.github.kgbis.remotecontrol.tray.ui.support;

import java.util.LinkedHashMap;
import java.util.Map;

public final class InformationModel {

	private final Map<String, String> addresses = new LinkedHashMap<>();

	public void update(Map<String, String> newData) {
		addresses.clear();
		addresses.putAll(newData);
	}

	public Map<String, String> getAddresses() {
		return Map.copyOf(addresses);
	}

	public int size() {
		return addresses.size();
	}

}
