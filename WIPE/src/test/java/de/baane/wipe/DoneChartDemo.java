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

public class DoneChartDemo extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) {
		// Initialize data
		FileControl.openLastFile();
		DataHolder d = DataIO.loadFromXMl(FileControl.SAVE_FILE);
		if (d == null) {
			System.err.println("No save file found.");
			return;
		}
		
		ArrayList<Character> characters = d.getCharacters();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (Character c : characters) {
			LinkedHashMap<Instance, RaidStatus> progresses = c.getProgresses();
			for (Instance i : progresses.keySet()) {
				RaidStatus status = progresses.get(i);
				String statusName = status.toString();
				if (!map.containsKey(statusName))
					map.put(statusName, 1);
				else
					map.put(statusName, map.get(statusName).intValue() + 1);
			}
		}
		
		ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
		for (String status : map.keySet()) {
			chartData.add(new PieChart.Data(status, map.get(status)));
		}
		
		// Initialize chart
		Scene scene = new Scene(new Group());
		stage.setTitle("Imported Fruits");
		stage.setWidth(500);
		stage.setHeight(500);
		
		final PieChart chart = new PieChart(chartData);
		chart.setTitle("Raid Status");
		chart.setLegendSide(Side.LEFT);
		
		((Group) scene.getRoot()).getChildren().add(chart);
		stage.setScene(scene);
		stage.show();
	}
}
