package de.baane.wipe.view;

import java.util.Optional;

import de.baane.wipe.MainFX;
import de.baane.wipe.model.CharacterClass;
import de.fhg.iml.vlog.ination.INation;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Pair;

public class AddCharacterDialog {
	
	private INation ination;

	public AddCharacterDialog(INation iNation) {
		this.ination = iNation;
	}

	public Pair<String, CharacterClass> show() {
		// Create the custom dialog.
		Dialog<Pair<String, CharacterClass>> dialog = new Dialog<>();
		dialog.setTitle(localize("Add character"));
		dialog.setHeaderText(localize("Please insert character informations"));
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

		TextField charName = new TextField();
		charName.setPromptText(localize("Character name"));
		ComboBox<CharacterClass> charClass = new ComboBox<>();
		charClass.getItems().addAll(CharacterClass.values());
		charClass.setValue(CharacterClass.DRUID);
		charClass.setPromptText(localize("Character class"));
		charClass.setCellFactory(new Callback<ListView<CharacterClass>, ListCell<CharacterClass>>() {
			@Override
			public ListCell<CharacterClass> call(ListView<CharacterClass> p) {
				return new ListCell<CharacterClass>() {
					private final Rectangle rectangle;
					{
						setContentDisplay(ContentDisplay.LEFT);
						rectangle = new Rectangle(10, 10);
					}
					
					@Override
					protected void updateItem(CharacterClass item,
							boolean empty) {
						super.updateItem(item, empty);
						
						if (item == null || empty) {
							setGraphic(null);
						} else {
							setText(item.toString());
							rectangle.setFill(item.getColor());
							rectangle.setStroke(Color.BLACK);
							setGraphic(rectangle);
						}
					}
				};
			}
		});

		grid.add(new Label(localize("Character name")), 0, 0);
		grid.add(charName, 1, 0);
		grid.add(new Label(localize("Character class")), 0, 1);
		grid.add(charClass, 1, 1);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		charName.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> charName.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new Pair<>(charName.getText(), charClass.getValue());
		    }
		    return null;
		});

		Optional<Pair<String, CharacterClass>> result = dialog.showAndWait();
		
		result.ifPresent(charNameClass -> {
		    System.out.println("Username=" + charNameClass.getKey() + ", Class=" + charNameClass.getValue());
		});
		return result.isPresent() ? result.get() : null;
	}
	
	private String localize(String text) {
		if(ination == null) return text;
		return ination.getTranslation(text);
	}
	
}
