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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

public class AddCharacterDialog {
	
	private INation iNation;

	public AddCharacterDialog(INation iNation) {
		this.iNation = iNation;
	}

	public Pair<String, CharacterClass> show() {
		Optional<Pair<String, CharacterClass>> result = initDialog().showAndWait();
		return result.isPresent() ? result.get() : null;
	}

	private Dialog<Pair<String, CharacterClass>> initDialog() {
		// Create the custom dialog.
		Dialog<Pair<String, CharacterClass>> dialog = new Dialog<>();
		dialog.setTitle(localize("Add character"));
		dialog.setHeaderText(localize("Please insert character informations"));
		dialog.initOwner(MainFX.INSTANCE);

		dialog.setGraphic(Message.getGraphic("add"));

		// Set the button types.
		ButtonType addButton = new ButtonType(localize("Add"), ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

		// Create the name and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 100, 10, 10));

		String characterName = localize("Character name");
		String characterClass = localize("Character class");
		TextField charName = new TextField();
		charName.setPromptText(characterName);
		ComboBox<CharacterClass> charClass = initCharClassBox();

		grid.add(new Label(characterName), 0, 0);
		grid.add(charName, 1, 0);
		grid.add(new Label(characterClass), 0, 1);
		grid.add(charClass, 1, 1);
		
		// Enable/Disable add button depending on whether a username was entered.
		Node addButtonNode = dialog.getDialogPane().lookupButton(addButton);
		addButtonNode.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		charName.textProperty().addListener((observable, oldValue, newValue) -> {
		    addButtonNode.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the name field by default.
		Platform.runLater(() -> charName.requestFocus());

		// Convert the result to a pair when the add button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton != addButton) return null;
		    return new Pair<>(charName.getText(), charClass.getValue());
		});
		return dialog;
	}

	private ComboBox<CharacterClass> initCharClassBox() {
		ComboBox<CharacterClass> charClass = new ComboBox<>();
		charClass.getItems().addAll(CharacterClass.values());
		charClass.setValue(CharacterClass.DRUID);
		charClass.setPromptText(localize("Character class"));
		
		charClass.setButtonCell(initCharClassListCell());
		charClass.setCellFactory(p -> initCharClassListCell());
		return charClass;
	}

	private ListCell<CharacterClass> initCharClassListCell() {
		return new ListCell<CharacterClass>() {
			private final Rectangle rectangle;
			{
				setContentDisplay(ContentDisplay.LEFT);
				rectangle = new Rectangle(10, 10);
			}
			
			@Override
			protected void updateItem(CharacterClass item, boolean empty) {
				super.updateItem(item, empty);
				
				if (item == null || empty) setGraphic(null);
				else {
					setText(item.toString());
					rectangle.setFill(item.getColor());
					rectangle.setStroke(Color.BLACK);
					setGraphic(rectangle);
				}
			}
		};
	}
	
	private String localize(String text) {
		if(iNation == null) return text;
		return iNation.getTranslation(text);
	}
	
}
