package de.baane.wipe.view;

import java.util.ArrayList;
import java.util.Optional;

import de.baane.wipe.MainFX;
import de.baane.wipe.model.Character;
import de.fhg.iml.vlog.ination.INation;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RemoveCharacterDialog {
	
	private INation iNation;

	public RemoveCharacterDialog(INation iNation) {
		this.iNation = iNation;
	}

	public Character show(ArrayList<Character> chars) {
		Optional<Character> result = initDialog(localize("character_name"), chars).showAndWait();
		return result.isPresent() ? result.get() : null;
	}

	private Dialog<Character> initDialog(String optionText, ArrayList<Character> chars) {
		// Create the custom dialog.
		Dialog<Character> dialog = new Dialog<>();
		dialog.initOwner(MainFX.INSTANCE);
		dialog.setTitle(localize("Remove character"));
		dialog.setHeaderText(localize("Remove character"));
		dialog.setContentText(optionText);
		
		dialog.setGraphic(Message.getGraphic("bin"));

		// Set the button types.
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		// Create the char box.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);

		ComboBox<Character> charBox = initCharBox(chars);

		grid.add(new Label(localize("Character name")), 0, 0);
		grid.add(charBox, 1, 0);
		dialog.getDialogPane().setContent(grid);
		
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton != ButtonType.OK) return null;
		    return charBox.getValue();
		});
		return dialog;
	}

	private ComboBox<Character> initCharBox(ArrayList<Character> chars) {
		ComboBox<Character> charBox = new ComboBox<>();
		charBox.getItems().addAll(chars);
		charBox.setValue(chars.get(0));
		
		charBox.setPromptText(localize("Character name"));
		charBox.setButtonCell(initCharClassListCell());
		charBox.setCellFactory(p -> initCharClassListCell());
		return charBox;
	}

	private ListCell<Character> initCharClassListCell() {
		return new ListCell<Character>() {
			private final Rectangle rectangle;
			{
				setContentDisplay(ContentDisplay.LEFT);
				rectangle = new Rectangle(10, 10);
			}
			
			@Override
			protected void updateItem(Character item, boolean empty) {
				super.updateItem(item, empty);
				
				if (item == null || empty) {
					setGraphic(null);
				} else {
					setText(item.toString());
					rectangle.setFill(item.getCharClass().getColor());
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
