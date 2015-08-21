package de.baane.wipe.util;

import java.util.Calendar;

import de.baane.wipe.control.FileControl;
import de.baane.wipe.control.data.PropertyIO;
import de.baane.wipe.view.Message;

public class FileUtil {
	
	public static void tuesdayWarning() {
		boolean warningOn = Boolean.valueOf(PropertyIO.readProperty(FileControl.WOW_WARN_TUESDAY));
		if (warningOn) {
			Calendar c = Calendar.getInstance();
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
				Message.showWarning("It's tuesday, dude! CHECK YOUR INSTANCE STATUS!");
		}
	}
	
}
