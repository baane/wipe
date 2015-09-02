package de.baane.wipe.view;

import java.util.List;
import java.util.Optional;

import de.baane.wipe.MainFX;
import de.fhg.iml.vlog.ination.INation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;

public class Message {
	private static final INation INATION = INation.openAndRegister(Message.class);
	
	public static String showInput(String title, String text) {
		TextInputDialog d = new TextInputDialog();
		d.initOwner(MainFX.INSTANCE);
		d.setGraphic(getGraphic(getGraphicName(AlertType.INFORMATION)));
		d.setTitle(null);
		d.setHeaderText(title);
		d.setContentText(text);
		Optional<String> option = d.showAndWait();
		
		return option.isPresent() ? option.get() : null;
	}

	public static String showInput(String title, String text, String iconName) {
		TextInputDialog d = new TextInputDialog();
		d.initOwner(MainFX.INSTANCE);
		d.setGraphic(getGraphic(iconName));
		d.setTitle(null);
		d.setHeaderText(title);
		d.setContentText(text);
		Optional<String> option = d.showAndWait();
		
		return option.isPresent() ? option.get() : null;
	}
	
	public static <T> T showOption(String title, String optionText, T defaultEntry, List<T> entries) {
		return showOption(title, optionText, defaultEntry, entries, getGraphicName(AlertType.CONFIRMATION));
	}
	
	public static <T> T showOption(String title, String optionText, T defaultEntry, List<T> entries, String iconName) {
		ChoiceDialog<T> d = new ChoiceDialog<T>(defaultEntry, entries);
		d.initOwner(MainFX.INSTANCE);
		if (iconName != null) d.setGraphic(getGraphic(iconName));
		d.setTitle(null);
		d.setHeaderText(title);
		d.setContentText(optionText);
		Optional<T> option = d.showAndWait();
		
		return option.isPresent() ? option.get() : null;
	}
	
	public static ButtonType showConfirm(String text) {
		Alert alert = createAlert(AlertType.INFORMATION);
		alert.setContentText(text);
		Optional<ButtonType> option = alert.showAndWait();
		
		return option.get();
	}
	public static ButtonType showConfirm(String title, String text) {
		Alert alert = createAlert(AlertType.INFORMATION);
		alert.setHeaderText(title);
		alert.setContentText(text);
		Optional<ButtonType> option = alert.showAndWait();
		
		return option.get();
	}
	
	public static ButtonType showYesNo(String text) {
		Alert alert = createAlert(AlertType.INFORMATION);
//		alert.setHeaderText(null);
		alert.setContentText(text);
		
		alert.getButtonTypes().setAll(yesBtn(), noBtn());
		
		Optional<ButtonType> option = alert.showAndWait();
		return option.isPresent() ? option.get() : null;
	}

	public static ButtonType showYesNoCancel(String text) {
		return showYesNoCancel(text, AlertType.INFORMATION);
	}

	public static ButtonType showYesNoCancel(String text, AlertType type) {
		Alert alert = createAlert(type);
		alert.setContentText(text);
		
		alert.getButtonTypes().setAll(yesBtn(), noBtn(), cancelBtn());
		
		Optional<ButtonType> option = alert.showAndWait();
		return option.isPresent() ? option.get() : null;
	}

	public static void showInformation(String text) {
		Alert alert = createAlert(AlertType.INFORMATION);
		alert.setContentText(text);
		alert.showAndWait();
	}
	
	public static void showWarning(String text) {
		Alert alert = createAlert(AlertType.WARNING);
		alert.setContentText(text);
		alert.showAndWait();
	}
	
	public static void showError(String text) {
		Alert alert = createAlert(AlertType.ERROR);
		alert.setContentText(text);
		alert.showAndWait();
	}

	private static Alert createAlert(AlertType type) {
		Alert alert = new Alert(type == null ? AlertType.INFORMATION : type);
		alert.setGraphic(getGraphic(getGraphicName(type)));
		if (MainFX.INSTANCE != null) alert.initOwner(MainFX.INSTANCE);
		alert.setTitle(null);
		return alert;
	}
	
	static String getGraphicName(AlertType type) {
		try {
			String iconName = null;
			if (AlertType.WARNING.equals(type))
				iconName = "warning";
			if (AlertType.INFORMATION.equals(type))
				iconName = "info";
			if (AlertType.ERROR.equals(type))
				iconName = "error";
			if (AlertType.CONFIRMATION.equals(type))
				iconName = "question"; //TODO
			
			return iconName;
		} catch (Exception e) {}
		return null;
	}
	
	static ImageView getGraphic(String iconName) {
		try {
			String png = ".png";
			if (!iconName.endsWith(png)) iconName += png; 
			
			String url = Message.class.getResource("icons/" + iconName).toString();
			ImageView imageView = new ImageView(url);
			int size = 32;
			imageView.setFitWidth(size);
			imageView.setFitHeight(size);
			return imageView;
		} catch (Exception e) {}
		return null;
	}


	private static ButtonType yesBtn() {
		return new ButtonType(localize("Yes"), ButtonData.YES);
	}
	private static ButtonType noBtn() {
		return new ButtonType(localize("No"), ButtonData.NO);
	}
	private static ButtonType cancelBtn() {
		return new ButtonType(localize("Cancel"), ButtonData.CANCEL_CLOSE);
	}
	
	private static String localize(String text) {
		return INATION.getTranslation(text);
	}
	
}
