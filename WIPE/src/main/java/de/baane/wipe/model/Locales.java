package de.baane.wipe.model;

import java.util.Locale;

import de.fhg.iml.vlog.ination.INation;

public enum Locales {
	GERMAN(Locale.GERMAN),
	ENGLISH(Locale.ENGLISH);
	
	private static final INation INATION = INation.openAndRegister(Locales.class);
	
	private Locale locale;
	
	Locales(Locale l) {
		this.locale = l;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	@Override
	public String toString() {
		return INATION.getTranslation(locale.toString());
	}
	
}
