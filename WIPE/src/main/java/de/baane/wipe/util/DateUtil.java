package de.baane.wipe.util;

import java.io.File;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import de.baane.wipe.control.FileControl;

public class DateUtil {
	
	public static boolean checkWednesdayReset() {
		File saveFile = FileControl.SAVE_FILE;
		if (saveFile == null || !saveFile.exists()) return false;
		
		Date saveDate = new Date(saveFile.lastModified());
		return resetIds(saveDate, new Date());
	}
	
	@SuppressWarnings("unused")
	private void checkDailyReset() {
		//TODO: implement daily reset
	}
	

	/** Wochentag des ID Resets */
	static DayOfWeek dayOfIdReset = DayOfWeek.WEDNESDAY;
	
	/**
	 * Ermittelt, ob IDs zurückgesetzt werden müssen. Berechnet dazu den letzten
	 * ID-Reset und vergleicht das Datum mit dem dem letzten Speicherstand.
	 */
	public static boolean resetIds(Date saveDate, Date loginDate) {
		LocalDateTime lastUpdateDate = parseDate(saveDate);
		LocalDateTime loginDateTime = parseDate(loginDate);
		LocalDateTime lastWeeklyIdRest = getLastWeeklyIdRest(loginDateTime);
		return lastWeeklyIdRest.isAfter(lastUpdateDate);
	}

	private static LocalDateTime getLastWeeklyIdRest(LocalDateTime loginDate) {
		/**
		 * Initialisiere den ID-Reset auf den Login und die Reset-Uhrzeit<br>
		 * Achtung: An dieser Stelle kann lastWeeklyIdRest > loginDate sein!
		 */
		LocalDate lastWeeklyIdRest = loginDate.toLocalDate();
		/** Uhrzeit des ID Resets */
		LocalDateTime lastWeeklyIdRestTime = lastWeeklyIdRest.atTime(6, 0, 0);
		
		/**
		 * Sind wir schon auf einem Mittwoch müssen wir nur unterscheiden, ob wir
		 * vor oder nach timeOfIdReset eingeloggt sind. im ersten Fall gehen
		 * wir eine Woche zurück im zweiten ist lastWeeklyIdRest schon korrekt
		 * gesetzt.
		 * 
		 * An jedem anderen Wochentag gehen wir einfach zum letzten Mittwoch
		 * zurück (Uhrzeit ist schon richtig gesetzt).
		 */
		if (loginDate.getDayOfWeek() == dayOfIdReset) {
			if (lastWeeklyIdRestTime.isAfter(loginDate))
				lastWeeklyIdRestTime = lastWeeklyIdRestTime.minusDays(7);
		} else {
			while (lastWeeklyIdRestTime.getDayOfWeek() != dayOfIdReset)
				lastWeeklyIdRestTime = lastWeeklyIdRestTime.minusDays(1);
		}
		return lastWeeklyIdRestTime;
	}
	
	/**
	 * Parser of doom! Needed for converting of Java 7 date to current date format.
	 */
	private static LocalDateTime parseDate(Date date) {
		Instant instant = Instant.ofEpochMilli(date.getTime());
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}
	
}
