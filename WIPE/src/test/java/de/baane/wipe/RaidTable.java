package de.baane.wipe;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.sun.javafx.scene.control.skin.CustomColorDialog;

import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import de.baane.wipe.model.DataHolder;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.RaidStatus;
import sun.launcher.resources.launcher;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class RaidTable extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	DataHolder d;
	
    @Override
    public void start(Stage primaryStage) {
		d = new DataHolder();
		
		
        ObservableList<Character> chars = initData();
        TableView<Character> charTable = new TableView(chars);
        TableColumn<Character, Object> nameColumn = new TableColumn("name");
            nameColumn.setCellValueFactory(new PropertyValueFactory("name"));
            charTable.getColumns().add(nameColumn);

        Callback<CellDataFeatures<Character, Object>, ObservableValue<Object>> callBack = 
                new Callback<TableColumn.CellDataFeatures<Character, Object>, ObservableValue<Object>>() {
            @Override
            public ObservableValue<Object> call(TableColumn.CellDataFeatures<Character, Object> param) {
				Instance userData = (Instance) param.getTableColumn().getUserData();
				Character value2 = param.getValue();
				return (ObservableValue<Object>) value2;
//				ObservableValue<Object> value = 
//						value2.getProgresses().containsKey(userData) 
//						? value2.getProgresses().get(userData).name()
//						: new SimpleStringProperty("");
//				return value;
            }
        };

        ObservableList<TableColumn<Character, Object>> assCols = FXCollections.observableArrayList();
        for (Instance i : d.getInstances()) {
            TableColumn<Character, Object> tmpCol = new TableColumn<>(i.getName());
            tmpCol.setUserData(i);
            tmpCol.setCellValueFactory(callBack);
            assCols.add(tmpCol);
        }
        charTable.getColumns().addAll(assCols);

        VBox root = new VBox(charTable);
        Scene scene = new Scene(root, 500, 250);

        primaryStage.setTitle("Table with map");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
	private ObservableList<Character> initData() {
		Character c1 = new Character(1, "Zottel", CharacterClass.WARLOCK);
		LinkedHashMap<Instance, RaidStatus> progresses = new LinkedHashMap<>();
		Instance instance = new Instance(0, "Test INi");
		d.addInstance(instance);
		progresses.put(instance, RaidStatus.CONFIRMED);
		c1.setProgresses(progresses);
		Character c2 = new Character(2, "Caelean", CharacterClass.PRIEST);
		return FXCollections.observableArrayList(c1, c2);
	}

//    public class Student {
//
//        private final StringProperty firstName = new SimpleStringProperty();
//        public StringProperty firstNameProperty(){return firstName;}
//        public final HashMap<String, Double> map;
//
//        public Student(String fn) {
//            firstName.set(fn);
//            map = new LinkedHashMap<>();
//            for (int i = 1; i <= 10; i++) {
//                double grade = Math.random();
//                if (grade > .5) {
//                    map.put("ass" + Integer.toString(i), grade);
//                }
//            }
//        }
//    }
}