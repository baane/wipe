package de.baane.wipe.control;

import de.baane.wipe.control.data.PropertyIO;
import javafx.stage.Stage;

/**
 * Saves and loads the windows size and position from properties.<br>
 * 
 * Loading is possible for swing and also FX components. 
 * 
 * @author Vanessa Tessarzik
 *
 */
public class WindowControl {
	
	public static void saveWindowPosition(double x, double y) {
		PropertyIO.saveToProperty("location-x", String.valueOf(x));
		PropertyIO.saveToProperty("location-y", String.valueOf(y));
	}
	
	public static void loadWindowPosition(Stage s) {
		String propX = PropertyIO.readProperty("location-x");
		String propY = PropertyIO.readProperty("location-y");
		int x = (int) (propX == null ? 0 : Double.valueOf(propX));
		int y = (int) (propY == null ? 0 : Double.valueOf(propY));
		s.setX(x);
		s.setY(y);
	}
	
	
	public static void saveWindowSize(double width, double height) {
		PropertyIO.saveToProperty("size-w", String.valueOf(width));
		PropertyIO.saveToProperty("size-h", String.valueOf(height));
	}
	
	public static void loadWindowSize(Stage s) {
		String propWidth = PropertyIO.readProperty("size-w");
		String propHeight = PropertyIO.readProperty("size-h");
		int width = (int) (propWidth == null ? 960 : Double.valueOf(propWidth));
		int height = (int) (propWidth == null ? 300 : Double.valueOf(propHeight));
		s.setWidth(width);
		s.setHeight(height);
	}
}
