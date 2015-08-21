package de.baane.wipe.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.RaidStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TableViewFX extends VBox {
	
	private TableView table;
	private ObservableList model;
	
	public TableViewFX() {
		initGUI();
	}
	
	private void initGUI() {
		this.getChildren().add(getTable());
	}
	
	public TableView getTable() {
		if (table == null) {
			table = new TableView<>();
			table.setEditable(true);
			table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//			table.getColumns().add(new TableColumn<>("Character"));
			
			table.prefWidthProperty().bind(this.prefWidthProperty());
			table.prefHeightProperty().bind(this.prefHeightProperty());
			table.resize(getWidth(), getHeight());
			
			super.layoutChildren();
		}
		return table;
	}
	
//	public DefaultTableModel getModel() {
//		if (model == null) {
//			String[] columnNames = { "Instance" };
//			model = new DefaultTableModel(null, columnNames);
//		}
//		return model;
//	}
	
	
	public void setModel(Object[][] content, String[] columnNames) {
//		getContentTest();
		
		getTable().getItems().clear();
		getTable().getColumns().clear();
		
		for (String string : columnNames) {
			string = string.equals("charakter") ? "name" : string;
			TableColumn col = new TableColumn<>(string);
			//TODO
//	        TableColumn emailCol = new TableColumn(string);
//	        emailCol.setMinWidth(200);
	        string = string.toLowerCase();
//	        emailCol.setCellValueFactory(
//	                new PropertyValueFactory<>(lowerCase));
			col.setCellValueFactory(
					new PropertyValueFactory<>(string));
			getTable().getColumns().add(col);
		}
//		ObservableList value = FXCollections.observableArrayList("Test");
//		table.setItems(value);
		for (int i = 0; i < content.length; i++) {
			Object[] row = content[i];
			ObservableList list = FXCollections.observableArrayList();
			for (int j = 0; j < row.length; j++) {
//				Object object = row[j];
				String object = String.valueOf(row[j]);
				Character ch = new Character(0, object, null);
				list.add(ch);
			}
			getTable().getItems().add(list);
		}
	}
	
	private TableView getContentTest() {
		table = new TableView();
//		table.setEditable(true);
		
		TableColumn idCol = new TableColumn("Id");
		TableColumn nameCol = new TableColumn<>("Name");
		TableColumn classCol = new TableColumn("Class");
		TableColumn<Character, LinkedHashMap<Instance, RaidStatus>> statusCol = new TableColumn<>("Progresses");
		idCol.setCellValueFactory(
				new PropertyValueFactory<>("Id"));
		
		nameCol.setCellValueFactory(
				new PropertyValueFactory<>("name"));
		
		classCol.setCellValueFactory(
				new PropertyValueFactory<>("charClass"));
		
		statusCol.setCellValueFactory(
				new PropertyValueFactory<>("progresses"));
		table.getColumns().addAll(idCol, nameCol, classCol, statusCol);
		
//		table.setItems(initData());
		
		return table;
	}
	
	private ObservableList<Character> initData() {
		de.baane.wipe.model.Character c1 = new de.baane.wipe.model.Character(1, "Zottel", CharacterClass.WARLOCK);
		LinkedHashMap<Instance, RaidStatus> progresses = new LinkedHashMap<Instance, RaidStatus>();
		progresses.put(new Instance(0, "Test INi"), RaidStatus.CONFIRMED);
		c1.setProgresses(progresses);
		de.baane.wipe.model.Character c2 = new de.baane.wipe.model.Character(2, "Caelean", CharacterClass.PRIEST);
		return FXCollections.observableArrayList(c1, c2);
	}

	public void setModel(ArrayList<Character> characters, String[] columnNames) {
		getContentTest();
		
//		getTable().getItems().clear();
//		getTable().getColumns().clear();
//		
//		for (String string : columnNames) {
//			TableColumn col = new TableColumn<>(string);
//			//TODO
////	        TableColumn emailCol = new TableColumn(string);
////	        emailCol.setMinWidth(200);
//	        String lowerCase = string.toLowerCase();
////	        emailCol.setCellValueFactory(
////	                new PropertyValueFactory<>(lowerCase));
//	        lowerCase = lowerCase.equals("charakter") ? "name" : lowerCase;
//			col.setCellValueFactory(
//					new PropertyValueFactory<>(lowerCase));
//			getTable().getColumns().add(col);
//		}
////		ObservableList value = FXCollections.observableArrayList("Test");
////		table.setItems(value);
//		ObservableList list = FXCollections.observableArrayList();
//		for (Character c : characters) {
////			for (int j = 0; j < row.length; j++) {
////				Object object = row[j];
//				list.add(c);
////			}
//		}
//		getTable().getItems().add(list);
	}
	
}
