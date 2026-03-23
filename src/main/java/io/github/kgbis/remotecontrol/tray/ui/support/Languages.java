package io.github.kgbis.remotecontrol.tray.ui.support;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

public enum Languages {

	ENGLISH("English", Locale.of("en")), SPANISH("Español", Locale.of("es"));

	@Getter
	private final Locale locale;

	@Getter
	private final String text;

	Languages(String text, Locale locale) {
		this.text = text;
		this.locale = locale;
	}

	/**
	 * Returns the enum constant matching the locale provided. It fallbacks to English if
	 * passed locale is null or if not found.<br>
	 * <b>Important:</b> Matches only by language, ignoring country/variant.
	 * @param locale Locale to search for
	 * @return the enum value
	 */
	public static Languages fromLocale(Locale locale) {
		if (locale == null)
			return Languages.ENGLISH;

		return Arrays.stream(Languages.values())
			.filter(l -> l.getLocale().getLanguage().equals(locale.getLanguage()))
			.findFirst()
			.orElse(Languages.ENGLISH);
	}

}
