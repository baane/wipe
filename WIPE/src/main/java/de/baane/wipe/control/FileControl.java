package de.baane.wipe.control;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import de.baane.wipe.MainFX;
import de.baane.wipe.control.data.DataIO;
import de.baane.wipe.control.data.PropertyIO;
import de.baane.wipe.model.DataHolder;
import de.baane.wipe.util.DateUtil;
import de.baane.wipe.view.Message;
import de.fhg.iml.vlog.ination.INation;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FileControl {
	public static final String WOW_LASTSAVEFILE = "wow.lastsavefile";
	public static final String WOW_WARN_TUESDAY = "wow.warnTuesday";
	public static final String EXTENSION = "*.raid";
	
	private static final INation INATION = INation.openAndRegister(FileControl.class);

	public static boolean isSaved = true;
	public static File SAVE_FILE;
	
	public static void openLastFile() {
		String property = PropertyIO.readProperty(WOW_LASTSAVEFILE);
		if(property != null) SAVE_FILE = new File(property);
	}
	
	public static File openLastDirectory() {
		openLastFile();
		if(SAVE_FILE == null ||!SAVE_FILE.exists()) SAVE_FILE = getCurrentPath();
		
		return SAVE_FILE.isFile() ? SAVE_FILE.getParentFile() : SAVE_FILE;
	}
	
	private static File getCurrentPath() {
		try {
			String path = MainFX.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			return new File(path);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File showLoadDialog(Window view) {
		FileChooser chooser = initChooserFX();
		chooser.setTitle(localize("load"));
		return chooser.showOpenDialog(view);
	}
	
	public static void save(DataHolder data) {
		File file = FileControl.SAVE_FILE;
		PropertyIO.saveToProperty(FileControl.WOW_LASTSAVEFILE, file.getAbsolutePath());
		try {
			DataIO.saveToXML(FileControl.SAVE_FILE, data);
		} catch (IOException e) {
			String message = localize("save_file_failed") + ":\n" + file;
			Message.showError(message);
			System.err.println(message);
			isSaved = false;
		}
		isSaved = true;
	}
	
	public static void saveAs(DataHolder data) {
		if (FileControl.showSaveAsDialog(MainFX.INSTANCE)) save(data);
	}
	
	private static boolean showSaveAsDialog(Window view) {
		FileChooser chooser = initChooserFX();
		chooser.setTitle(localize("save_as"));
		File f = chooser.showSaveDialog(view);
		if(f == null) return false;
		
		FileControl.SAVE_FILE = f;
		return true;
	}
	
	public static boolean checkSaved(DataHolder data) {
		if (!isSaved) {
			ButtonType option = Message.showYesNoCancel(localize("save_question"));
			if (ButtonType.YES.getButtonData().equals(option.getButtonData())) {
				save(data);
				return isSaved;
			} else if (ButtonType.NO.getButtonData().equals(option.getButtonData())) {
				return true;
			} else if (ButtonType.CANCEL.getButtonData().equals(option.getButtonData())) {
				return false;
			}
		}
		return isSaved;
	}
	
	public static boolean checkSaveStatus() {
		if (DateUtil.checkWednesdayReset()) {
			String question = localize("check_save_status");
			if (Message.showYesNo(question).getButtonData().equals(ButtonType.YES.getButtonData()))
				return true;
		}
		return false;
	}

	private static FileChooser initChooserFX() {
		FileChooser chooser = new FileChooser();
		chooser.setInitialDirectory(FileControl.openLastDirectory());
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("WIPE", EXTENSION));
		return chooser;
	}

	private static String localize(String text) {
		return INATION.getTranslation(text);
	}
}
