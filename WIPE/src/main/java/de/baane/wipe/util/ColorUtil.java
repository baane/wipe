package de.baane.wipe.util;

import javafx.scene.paint.Color;

public class ColorUtil {
	
	/**
	 * Converts the given FX color to awt color.
	 * 
	 * @param color - FX
	 * @return color - Awt
	 */
	public static java.awt.Color getAwtColor(Color color) {
		return new java.awt.Color(
				(int)color.getRed(), 
				(int)color.getGreen(), 
				(int)color.getBlue(), 
				(int)color.getOpacity());
	}
	
	/**
	 * Return the given color to hex code.
	 * E.g. Color white is #FFFFFF.
	 * 
	 * @param color
	 * @return hex color String
	 */
	public static String getRGBCode(Color color) {
		return String.format("#%02X%02X%02X", 
				(int) (color.getRed() * 255),
				(int) (color.getGreen() * 255), 
				(int) (color.getBlue() * 255));
	}
}
