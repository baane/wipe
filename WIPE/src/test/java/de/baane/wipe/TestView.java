package de.baane.wipe;

import java.util.LinkedHashMap;

import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.RaidStatus;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class TestView extends Application {
    
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new Group());
        stage.setTitle("View Sample");
        stage.setWidth(600);
        stage.setHeight(500);
 
		Group group = (Group) scene.getRoot();
		group.getChildren().addAll(getContentTestC());

        stage.setScene(scene);
        stage.show();
	}
	
	private Region getContentTest() {
		final TableView table = new TableView();
		table.setEditable(true);
		
		TableColumn idCol = new TableColumn("Id");
		TableColumn nameCol = new TableColumn<>("Name");
		TableColumn classCol = new TableColumn("Class");
		TableColumn<Character, LinkedHashMap<Instance, RaidStatus>> statusCol = new TableColumn<>("Progresses");
		idCol.setCellValueFactory(
				new PropertyValueFactory<>("id"));
		
		nameCol.setCellValueFactory(
				new PropertyValueFactory<>("name"));
		
		classCol.setCellValueFactory(
				new PropertyValueFactory<>("charClass"));
		
		statusCol.setCellValueFactory(
				new PropertyValueFactory<>("progresses"));
		table.getColumns().addAll(idCol, nameCol, classCol, statusCol);
		
		table.setItems(initData());
		
		return table;
	}
	private Region getContentTestC() {
		final TableView table = new TableView();
		table.setEditable(true);
		
		TableColumn idCol = new TableColumn("Id");
		TableColumn nameCol = new TableColumn<>("Name");
		TableColumn classCol = new TableColumn("Class");
		TableColumn<Character, LinkedHashMap<Instance, RaidStatus>> statusCol = new TableColumn<>("Progresses");
		idCol.setCellValueFactory(
				new PropertyValueFactory<>("id"));
		
		nameCol.setCellValueFactory(
				new PropertyValueFactory<>("name"));
		
		classCol.setCellValueFactory(
				new PropertyValueFactory<>("charClass"));
		
		statusCol.setCellValueFactory(
				new PropertyValueFactory<>("progresses"));
//		statusCol.setCellValueFactory(new ObservableMapValueFactory<RaidStatus>("progresses"));
		table.getColumns().addAll(idCol, nameCol, classCol, statusCol);
		
		
		table.setItems(initData());
		
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
	
}
