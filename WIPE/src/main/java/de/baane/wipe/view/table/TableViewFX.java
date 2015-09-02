package de.baane.wipe.view.table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.sun.javafx.scene.control.skin.TableHeaderRow;

import de.baane.wipe.control.FileControl;
import de.baane.wipe.model.Character;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.RaidStatus;
import de.baane.wipe.view.RaidStatusCellFactory;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

@SuppressWarnings("restriction")
public class TableViewFX extends VBox {
	
	private TableView<Character> table;
	
	public TableViewFX() {
		initGUI();
	}
	
	private void initGUI() {
		this.getChildren().add(getTable());
	}
	
	public TableView<Character> getTable() {
		if (table == null) {
			table = new TableView<>();
			table.setEditable(true);
			table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			
			table.prefWidthProperty().bind(this.prefWidthProperty());
			table.prefHeightProperty().bind(this.prefHeightProperty());
			table.resize(getWidth(), getHeight());
			
			table.widthProperty().addListener(new ChangeListener<Object>() {
				@Override
				public void changed(
						ObservableValue<? extends Object> observable,
						Object oldValue, Object newValue) {
					TableHeaderRow header = (TableHeaderRow) table.lookup("TableHeaderRow");
					header.reorderingProperty().addListener(new ChangeListener<Boolean>() {
						@Override
						public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
							header.setReordering(false);
						}
					});
				}
			});
			
		}
		return table;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setModel(ArrayList<Character> characters, String[] columnNames) {
		getTable().getItems().clear();
		getTable().getColumns().clear();
		
		// Add columns
		for (String colName : columnNames) {
			TableColumn col = new TableColumn<>(colName);
			if (colName.equals(columnNames[0])) {
				col.setCellFactory(new CharacterCellFactory());
				col.setCellValueFactory(initStringCellFactory(columnNames));
			} else {
				col.setCellValueFactory(initEnumCellFactory(columnNames));
				col.setCellFactory(new RaidStatusCellFactory());
				col.setOnEditCommit(
						new EventHandler<CellEditEvent<Character, RaidStatus>>() {
							@Override
							public void handle(CellEditEvent<Character,RaidStatus> t) {
								String columnName = t.getTableView().getColumns().get(t.getTablePosition().getColumn()).getText();
								Character character = t.getTableView().getItems().get(t.getTablePosition().getRow());
								LinkedHashMap<Instance, RaidStatus> progresses = character.getProgresses();
								for (Entry<Instance, RaidStatus> e : progresses.entrySet()) {
									Instance instanceKey = e.getKey();
									if (columnName.equals(instanceKey.getName())) {
										RaidStatus status = t.getNewValue();
										progresses.put(instanceKey, status);
										FileControl.isSaved = false;
									}
								}
							}
						}
				);
			}
			
			getTable().getColumns().add(col);
		}
		
		// Add data
		for (Character c : characters) {
			getTable().getItems().add(c);
		}
	}

	private Callback<CellDataFeatures<Character, Object>, ObservableValue<String>> 
	initStringCellFactory(String[] columnNames) {
		return param -> {
			String columnName = param.getTableColumn().getText();
			if (columnName.startsWith(columnNames[0]))
					return new SimpleStringProperty(param.getValue().getName());
			
			LinkedHashMap<Instance,RaidStatus> progresses = param.getValue().getProgresses();
			for (Instance i : progresses.keySet()) {
				if (!i.getName().equals(param.getTableColumn().getText())) continue;
				RaidStatus raidStatus = progresses.get(i);
				if (raidStatus != null) {
					return new SimpleStringProperty(raidStatus.toString());
				}
			}
			return new SimpleStringProperty(RaidStatus.DEFAULT.name());
		};
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Callback<CellDataFeatures<Character, RaidStatus>, ObservableValue<RaidStatus>>
	initEnumCellFactory(String[] columnNames) {
		return param -> {
			String columnName = param.getTableColumn().getText();
			if (columnName.startsWith(columnNames[0]))
				return new SimpleObjectProperty(param.getValue().getName());
			
			LinkedHashMap<Instance,RaidStatus> progresses = param.getValue().getProgresses();
			for (Instance i : progresses.keySet()) {
				if (!i.getName().equals(param.getTableColumn().getText())) continue;
				RaidStatus raidStatus = progresses.get(i);
				if (raidStatus != null) {
					return new SimpleObjectProperty(raidStatus);
				}
			}
			return new SimpleObjectProperty(RaidStatus.DEFAULT);
		};
	}
	
}
