package io.github.kgbis.remotecontrol.tray.ui.support;

import lombok.Getter;

public enum NotificationDuration {

	SHORT(2000, "notification.ttl.short"), MEDIUM(4000, "notification.ttl.medium"), LONG(8000, "notification.ttl.long"),
	STICKY(0, "notification.ttl.sticky");

	@Getter
	private final int ttl;

	@Getter
	private final String textKey;

	NotificationDuration(int ttl, String textKey) {
		this.ttl = ttl;
		this.textKey = textKey;
	}

}
