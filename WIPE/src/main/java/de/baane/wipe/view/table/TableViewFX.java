package de.baane.wipe.view.table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import de.baane.wipe.control.FileControl;
import de.baane.wipe.model.Character;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.RaidStatus;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.util.Callback;

@SuppressWarnings("restriction")
public class TableViewFX extends TableView<Character> {
	
	public TableViewFX() {
		init();
	}
	
	private void init() {
		this.setEditable(true);
		this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		// TODO: Only because of reordering... Look for another solution.
		this.widthProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> {
			TableHeaderRow header = (TableHeaderRow) this.lookup("TableHeaderRow");
			header.reorderingProperty().addListener(
					(ChangeListener<Boolean>) (observable1, oldValue1, newValue1) -> 
						header.setReordering(false));
		});
	}
	
	public void setModel(ArrayList<Character> characters, String[] columnNames) {
		// Add columns
		this.getColumns().clear();
		for (String colName : columnNames) {
			TableColumn<Character, Object> col = new TableColumn<>(colName);
			col.setCellValueFactory(initEnumCellFactory(columnNames));
			if (colName.equals(columnNames[0])) {
				// Use normal text for Character name in first column
				col.setCellFactory(new CharacterCellFactory());
			} else {
				// Use ComboBox for choosable RaidStatus in every else column
				col.setCellFactory(new RaidStatusCellFactory());
				col.setOnEditCommit(editEventHandler());
			}
			
			this.getColumns().add(col);
		}
		
		// Add data
		this.getItems().clear();
		for (Character c : characters) this.getItems().add(c);
	}

	private EventHandler<CellEditEvent<Character, Object>> editEventHandler() {
		return t -> {
			TablePosition<Character, Object> tablePosition = t.getTablePosition();
			TableView<Character> tableView = t.getTableView();
			String columnName = tableView.getColumns().get(tablePosition.getColumn()).getText();
			Character character = tableView.getItems().get(tablePosition.getRow());
			
			LinkedHashMap<Instance, RaidStatus> progresses = character.getProgresses();
			for (Entry<Instance, RaidStatus> e : progresses.entrySet()) {
				Instance instanceKey = e.getKey();
				if (columnName.equals(instanceKey.getName())) {
					RaidStatus status = (RaidStatus) t.getNewValue();
					progresses.put(instanceKey, status);
					FileControl.isSaved = false;
				}
			}
		};
	}

	private Callback<CellDataFeatures<Character, Object>, ObservableValue<Object>>
	initEnumCellFactory(String[] columnNames) {
		return param -> {
			String columnName = param.getTableColumn().getText();
			if (columnName.startsWith(columnNames[0]))
				return new SimpleObjectProperty<Object>(param.getValue().getName());
			
			LinkedHashMap<Instance, RaidStatus> progresses = param.getValue().getProgresses();
			for (Instance i : progresses.keySet()) {
				if (!i.getName().equals(columnName)) continue;
				RaidStatus raidStatus = progresses.get(i);
				if (raidStatus != null)
					return new SimpleObjectProperty<Object>(raidStatus);
			}
			return new SimpleObjectProperty<Object>(RaidStatus.DEFAULT);
		};
	}
	
}
