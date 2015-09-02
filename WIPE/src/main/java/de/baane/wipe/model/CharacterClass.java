package de.baane.wipe.model;

import de.fhg.iml.vlog.ination.INation;
import javafx.scene.paint.Color;

public enum CharacterClass {
	DRUID(Color.valueOf("#FF7D0A")),
	HUNTER(Color.valueOf("#ABD473")),
	MAGE(Color.valueOf("#69CCF0")),
	PALADIN(Color.valueOf("#F58CBA")),
	PRIEST(Color.WHITE),
	ROGUE(Color.valueOf("#FFF569")),
	SHAMAN(Color.valueOf("#0070DE")),
	WARLOCK(Color.valueOf("#9482C9")),
	WARRIOR(Color.valueOf("#C79C6E")),
	DEATH_KNIGHT(Color.valueOf("#C41F3B"));

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
