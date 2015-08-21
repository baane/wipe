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
	
	private INation ination;

	public AddInstanceDialog(INation iNation) {
		this.ination = iNation;
	}

	public Pair<String, InstanceResetType> show() {
		// Create the custom dialog.
		Dialog<Pair<String, InstanceResetType>> dialog = new Dialog<>();
		dialog.setTitle(localize("Add instance"));
		dialog.setHeaderText(localize("Please insert instance informations"));
		dialog.initOwner(MainFX.INSTANCE);

		// Set the icon (must be included in the project).
		dialog.setGraphic(Message.getGraphic("add"));

		// Set the button types.
		ButtonType loginButtonType = new ButtonType(localize("Add"), ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField iniName = new TextField();
		iniName.setPromptText(localize("instance_name"));
		ComboBox<InstanceResetType> iniResetType = new ComboBox<>();
		iniResetType.getItems().addAll(InstanceResetType.values());
		iniResetType.setValue(InstanceResetType.WEEKLY);
		iniResetType.setPromptText(localize("instance_reset_type"));
		

		grid.add(new Label(localize("instance_name")), 0, 0);
		grid.add(iniName, 1, 0);
		grid.add(new Label(localize("instance_reset_type")), 0, 1);
		grid.add(iniResetType, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		iniName.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> iniName.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new Pair<>(iniName.getText(), iniResetType.getValue());
		    }
		    return null;
		});

		Optional<Pair<String, InstanceResetType>> result = dialog.showAndWait();
		
		result.ifPresent(iniNameResetType-> {
		    System.out.println("Ininame=" + iniNameResetType.getKey() + ", Reset=" + iniNameResetType.getValue());
		});
		return result.isPresent() ? result.get() : null;
	}
	
	private String localize(String text) {
		if(ination == null) return text;
		return ination.getTranslation(text);
	}
	
}
