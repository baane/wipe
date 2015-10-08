package de.baane.wipe.view;

import java.util.Optional;

import de.baane.wipe.MainFX;
import de.baane.wipe.model.Locales;
import de.fhg.iml.vlog.ination.INation;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;

public class ChooseLanguageDialog {
	
	private INation iNation;

	public ChooseLanguageDialog(INation iNation) {
		this.iNation = iNation;
	}

	public Locales show() {
		Optional<Locales> result = initDialog().showAndWait();
		return result.isPresent() ? result.get() : null;
	}

	private Dialog<Locales> initDialog() {
		// Create the custom dialog.
		Dialog<Locales> dialog = new Dialog<>();
		dialog.setTitle(localize("language"));
		dialog.setHeaderText(localize("select_language"));
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

		String locales = localize("language");
		ComboBox<Locales> localesBox = initLocalesBox();

		grid.add(new Label(locales), 0, 0);
		grid.add(localesBox, 1, 0);
		
		// Enable/Disable add button depending on whether a username was entered.
		Node addButtonNode = dialog.getDialogPane().lookupButton(addButton);
		addButtonNode.setDisable(true);

		dialog.getDialogPane().setContent(grid);

		// Convert the result to a pair when the add button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton != addButton) return null;
		    return localesBox.getValue();
		});
		return dialog;
	}

	private ComboBox<Locales> initLocalesBox() {
		ComboBox<Locales> localesBox = new ComboBox<>();
		localesBox.getItems().addAll(Locales.values());
		localesBox.setValue(Locales.GERMAN);
		localesBox.setPromptText(localize("language"));
		
		localesBox.setButtonCell(initCharClassListCell());
		localesBox.setCellFactory(p -> initCharClassListCell());
		return localesBox;
	}

	private ListCell<Locales> initCharClassListCell() {
		return new ListCell<Locales>() {
			{
				setContentDisplay(ContentDisplay.LEFT);
			}
			
			@Override
			protected void updateItem(Locales item, boolean empty) {
				super.updateItem(item, empty);
				
				if (item == null || empty) {
					setGraphic(null);
				} else {
					setText(item.toString());
					setGraphic(Message.getGraphic(item.name(), 16));
				}
			}
		};
	}
	
	private String localize(String text) {
		if(iNation == null) return text;
		return iNation.getTranslation(text);
	}
	
}
