package de.baane.wipe.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.baane.wipe.model.Character;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.RaidStatus;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class TableViewFX extends VBox {
	
	private TableView<Character> table;
	private ObservableList model;
	private ObservableList list;
	
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
		}
		return table;
	}
	
	public void setModel(ArrayList<Character> characters, String[] columnNames) {
		getTable().getItems().clear();
		getTable().getColumns().clear();
		
		// Add columns
		for (String string : columnNames) {
			TableColumn col = new TableColumn<>(string);
			
			col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Character, Object>,
					ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(TableColumn.CellDataFeatures<Character, Object> param) {
					String columnName = param.getTableColumn().getText();
					if (columnName.startsWith("Chara"))
							return new SimpleStringProperty(param.getValue().getName());
					
					LinkedHashMap<Instance,RaidStatus> progresses = param.getValue().getProgresses();
					for (Instance i : progresses.keySet()) {
						if (!i.getName().equals(param.getTableColumn().getText())) continue;
						RaidStatus raidStatus = progresses.get(i);
						if (raidStatus != null) {
							String value = raidStatus.name();
							return new SimpleStringProperty(value);
						}
					}
					return new SimpleStringProperty(RaidStatus.SUBSTITUTES_BENCH.name());
				}
			});
			
			getTable().getColumns().add(col);
		}
		
		// Add data
		for (Character c : characters) {
			getTable().getItems().add(c);
		}
	}
	
}
