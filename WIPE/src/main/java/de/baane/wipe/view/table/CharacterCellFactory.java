package de.baane.wipe.view.table;

import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

public class CharacterCellFactory
implements Callback<TableColumn<Character, Object>, TableCell<Character, Object>> {
	
	@Override
	public TableCell<Character, Object> call(TableColumn<Character, Object> p) {
		return new TableCell<Character, Object>() {
			@Override
			public void updateItem(Object item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? null : getString());
				
				@SuppressWarnings("unchecked")
				TableRow<Character> currentRow = getTableRow();
				Character character = currentRow == null ? null
						: currentRow.getItem();
				if (character != null) {
					CharacterClass charClass = character.getCharClass();
					
					int colIndex = getTableView().getColumns().indexOf(getTableColumn());
					if (colIndex == 0) {
						Rectangle rectangle = new Rectangle(10, 10);
						rectangle.setFill(charClass.getColor());
						rectangle.setStroke(Color.BLACK);
						setGraphic(rectangle);
					}
				}
			}
			
			private String getString() {
				return getItem() == null ? "" : getItem().toString();
			}
		};
	}
}