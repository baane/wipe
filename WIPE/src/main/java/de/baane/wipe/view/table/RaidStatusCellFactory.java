package de.baane.wipe.view.table;

import de.baane.wipe.model.Character;
import de.baane.wipe.model.RaidStatus;
import de.baane.wipe.util.ColorUtil;
import javafx.collections.FXCollections;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class RaidStatusCellFactory
implements Callback<TableColumn<Character, Object>, TableCell<Character, Object>> {
	
	@Override
	public TableCell<Character, Object> call(TableColumn<Character, Object> param) {
		ComboBoxTableCell<Character, Object> boxTableCell = new ComboBoxTableCell<Character, Object>() {
			@Override
			public void updateItem(Object status, boolean empty) {
				super.updateItem(status, empty);
				
				if (status != null && status instanceof RaidStatus) {
					RaidStatus s = (RaidStatus) status;
					String statusColor = ColorUtil.getRGBCode(s.getColor());
					setStyle("-fx-background-color: " + statusColor);
					setTextFill(Color.BLACK);
				}
			}
		};
		boxTableCell.getItems().clear();
		boxTableCell.getItems().addAll(FXCollections.observableArrayList(RaidStatus.values()));
		return boxTableCell;
	}
}