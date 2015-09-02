package de.baane.wipe.view;

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
implements Callback<TableColumn<Character, RaidStatus>, TableCell<Character, RaidStatus>> {
	
	@Override
	public TableCell<Character, RaidStatus> call(TableColumn<Character, RaidStatus> param) {
		return new ComboBoxTableCell<Character, RaidStatus>(FXCollections.observableArrayList(RaidStatus.values())) {
			@Override
			public void updateItem(RaidStatus status, boolean empty) {
				super.updateItem(status, empty);
				
				if (status != null) {
					 String statusColor = ColorUtil.getRGBCode(status.getColor());
					 setStyle("-fx-background-color: " + statusColor);
					setTextFill(Color.BLACK);
				}
			}
		};
	}
}