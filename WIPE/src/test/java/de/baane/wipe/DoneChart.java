package de.baane.wipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import de.baane.wipe.control.FileControl;
import de.baane.wipe.control.data.DataIO;
import de.baane.wipe.model.Character;
import de.baane.wipe.model.DataHolder;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.RaidStatus;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

public class DoneChart extends Application {
	
	DataHolder d = new DataHolder();
	@Override
	public void start(Stage stage) {
		// Init data
		FileControl.openLastFile();
		d = DataIO.loadFromXMl(FileControl.SAVE_FILE);
 
        ArrayList<Character> characters = d.getCharacters();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (Character c : characters) {
        	LinkedHashMap<Instance,RaidStatus> progresses = c.getProgresses();
        	for (Instance i : progresses.keySet()) {
        		RaidStatus status = progresses.get(i);
        		String name = status.toString();
        		if (!map.containsKey(name)) map.put(name, 1);
        		else map.put(name, map.get(name).intValue() + 1);
        	}
		}
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (String status : map.keySet()) {
        	pieChartData.add(new PieChart.Data(status, map.get(status)));
		}
        
        // Init chart
        Scene scene = new Scene(new Group());
        stage.setTitle("Imported Fruits");
        stage.setWidth(500);
        stage.setHeight(500);
        
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Raid Status");
        chart.setLegendSide(Side.LEFT);

        ((Group) scene.getRoot()).getChildren().add(chart);
        stage.setScene(scene);
        stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
