package de.baane.wipe.model;

import java.awt.Color;

import de.fhg.iml.vlog.ination.INation;

public enum CharacterClass {
	DRUID(Color.decode("#FF7D0A")),
	HUNTER(Color.decode("#ABD473")),
	MAGE(Color.decode("#69CCF0")),
	PALADIN(Color.decode("#F58CBA")),
	PRIEST(Color.WHITE),
	ROGUE(Color.decode("#FFF569")),
	SHAMAN(Color.decode("#0070DE")),
	WARLOCK(Color.decode("#9482C9")),
	WARRIOR(Color.decode("#C79C6E")),
	DEATH_KNIGHT(Color.decode("#C41F3B"));

	private static final INation INATION = INation.openAndRegister(CharacterClass.class);
	
	private Color color;
	
	CharacterClass(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	@Override
	public String toString() {
		return INATION.getTranslation(name());
	}
	
	public static void main(String[] args) {
		for (CharacterClass s : CharacterClass.values()) {
			System.out.println(s);
		}
	}
}
