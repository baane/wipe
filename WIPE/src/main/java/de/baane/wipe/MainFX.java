package de.baane.wipe;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import de.baane.wipe.control.FileControl;
import de.baane.wipe.control.TableControl;
import de.baane.wipe.control.WindowControl;
import de.baane.wipe.control.data.PropertyIO;
import de.baane.wipe.view.FileMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainFX extends Application {
	
	public static void main(String[] args) {
		PropertyIO.getLocale();
		FileControl.openLastFile();
		
		launch(args);
	}
	
	public static Stage INSTANCE;
	
	@Override
	public void start(Stage stage) throws Exception {
		Scene scene = new Scene(initGUI(stage), 500, 500);
		URL css = MainFX.class.getResource("style.css");
		if (css != null) scene.getStylesheets().add(css.toExternalForm());
		
		stage.setScene(scene);
		stage.show();
		
		INSTANCE = stage;
		initStage();
	}
	
	private VBox initGUI(Stage stage) {
		TableControl c = new TableControl();
		c.checkSaveStatus();
		
		// Initialize menu
		FileMenu menuBar = new FileMenu(c);
		
		VBox pane = new VBox();
		pane.getChildren().add(menuBar);
		pane.getChildren().add(c.askView());
		
		// Add listener for saving window properties and checking save status 
		stage.setOnCloseRequest(e -> {
			WindowControl.saveWindowPosition(stage.getX(), stage.getY());
			WindowControl.saveWindowSize(stage.getWidth(), stage.getHeight());
				
			if(FileControl.checkSaved(c.getData())) System.exit(0);
		});
		
		return pane;
	}

	private void initStage() {
		// Setting title of window
		INSTANCE.setTitle("WIPE - WoW Instance Progress Manager");
		
		// Add window icon, if available
		try {
			InputStream stream = MainFX.class.getResourceAsStream("Wipe.png");
			if (stream != null) {
				Image image = new Image(stream);
				INSTANCE.getIcons().add(image);
				stream.close();
			} else 
				System.err.println("Couldn't load stage icon.");
		} catch (IOException e) {
			System.err.println("Error while setting window icon.");
		}
		
		// Ask last window properties
		WindowControl.loadWindowPosition(INSTANCE);
		WindowControl.loadWindowSize(INSTANCE);
	}
	
}
