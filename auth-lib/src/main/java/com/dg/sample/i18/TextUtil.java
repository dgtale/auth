package com.dg.sample.i18;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class TextUtil {

	private static Locale defaultLocale = Locale.ENGLISH;
	private static List<String> availableLanguages = Arrays.asList("en");

	public static Locale getLocale(String lang) {
		if (availableLanguages.contains(lang)) {
			return new Locale(lang);
		}
		return defaultLocale;
	}

	public static Locale getLocale(List<Locale> clientAcceptableLanguages) {
		for (Locale locale : clientAcceptableLanguages) {
			if (availableLanguages.contains(locale.getLanguage())) {
				return locale;
			}
		}
		return defaultLocale;
	}
}
