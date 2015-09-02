package de.baane.wipe.model;

import de.fhg.iml.vlog.ination.INation;
import javafx.scene.paint.Color;

public enum RaidStatus {
	/**
	 * Free<br>
	 * What to do, what to do...
	 */
	DEFAULT(Color.WHITE),
	/** 
	 * Over and done. Finally. 
	 */
	DONE(Color.LIMEGREEN),
	/** 
	 * Bestätigt<br>
	 * Guild lead had confirmed your subscribe.
	 */
	CONFIRMED(Color.GREENYELLOW),
	/**
	 * Angemeldet<br>
	 * You have subscribed in raid planner. Isn't confirmed now. But you
	 * wouldn't do anything, because you are also on substitutes bench.
	 */
	SUBSCRIBE(Color.GOLD),
	/** 
	 * Abgemeldet<br>
	 * Free for random raids!
	 */
	// SIGNED_OUT(Color.RED),
	/** 
	 * Ersatzbank<br>
	 * Maybe free for all, maybe not.
	 */
	SUBSTITUTES_BENCH(Color.LIGHTPINK);
	
	private static final INation INATION = INation.openAndRegister(RaidStatus.class);
	
	private Color color;
	
	RaidStatus(Color color) {
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
		for (RaidStatus s : RaidStatus.values()) {
			System.out.println(s);
		}
	}
}
