package de.baane.wipe.view;

import java.util.Optional;

import de.baane.wipe.MainFX;
import de.baane.wipe.model.InstanceResetType;
import de.fhg.iml.vlog.ination.INation;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class AddInstanceDialog {
	
	private INation iNation;

	public AddInstanceDialog(INation iNation) {
		this.iNation = iNation;
	}
	
	public Pair<String, InstanceResetType> show() {
		Optional<Pair<String, InstanceResetType>> result = initDialog().showAndWait();
		result.ifPresent(iniNameResetType-> {
		    System.out.println("Ininame=" + iniNameResetType.getKey() + ", Reset=" + iniNameResetType.getValue());
		});
		return result.isPresent() ? result.get() : null;
	}
	
	private Dialog<Pair<String, InstanceResetType>> initDialog() {
		// Create the custom dialog.
		Dialog<Pair<String, InstanceResetType>> dialog = new Dialog<>();
		dialog.setTitle(localize("Add instance"));
		dialog.setHeaderText(localize("Please insert instance informations"));
		dialog.initOwner(MainFX.INSTANCE);

		// Set the icon (must be included in the project).
		dialog.setGraphic(Message.getGraphic("add"));

		// Set the button types.
		ButtonType addButtonType = new ButtonType(localize("Add"), ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		String instanceName = localize("instance_name");
		String instanceResetType = localize("instance_reset_type");
		TextField name = new TextField();
		name.setPromptText(instanceName);
		ComboBox<InstanceResetType> resetType = new ComboBox<>();
		resetType.getItems().addAll(InstanceResetType.values());
		resetType.setValue(InstanceResetType.WEEKLY);
		resetType.setPromptText(instanceResetType);
		

		grid.add(new Label(instanceName), 0, 0);
		grid.add(name, 1, 0);
		grid.add(new Label(instanceResetType), 0, 1);
		grid.add(resetType, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(addButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		name.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> name.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton != addButtonType) return null;
		    return new Pair<>(name.getText(), resetType.getValue());
		});

		return dialog;
	}
	
	private String localize(String text) {
		if(iNation == null) return text;
		return iNation.getTranslation(text);
	}
	
}
