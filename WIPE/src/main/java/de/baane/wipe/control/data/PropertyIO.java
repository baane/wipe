package de.baane.wipe.control.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.baane.wipe.model.Locales;
import de.fhg.iml.vlog.ination.INation;

public class PropertyIO {
	private static final File PROPERTIES_FILE = new File("baumschmuser.properties");
	
	public static Locales LOCALE = Locales.GERMAN;
	public static final String LANGUAGE = "LANGUAGE";

	public static void saveToProperty(String key, String value) {
		FileOutputStream out = null;
		try {
			Properties props = getAllProperties();
			if(key != null && value != null)
				props.setProperty(key, value);
			out = new FileOutputStream(PROPERTIES_FILE);
			props.store(out, null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Properties getAllProperties() {
		Properties props = new Properties();
		InputStream out = null;
		try {
			if (!PROPERTIES_FILE.exists()) PROPERTIES_FILE.createNewFile();
			out = new FileInputStream(PROPERTIES_FILE);
			props.load(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return props;
	}

	public static String readProperty(String key) {
		return getAllProperties().getProperty(key);
	}
	
	public static void getLocale() {
		String locale = PropertyIO.readProperty(LANGUAGE);
		if (locale == null) return;
		PropertyIO.LOCALE = Locales.valueOf(locale);
		INation.setGlobalLocale(PropertyIO.LOCALE.getLocale());
	}
	
}