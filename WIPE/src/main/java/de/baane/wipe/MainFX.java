package de.baane.wipe;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.SwingUtilities;

import de.baane.wipe.control.FileControl;
import de.baane.wipe.control.TableControlSwing;
import de.baane.wipe.control.WindowControl;
import de.baane.wipe.control.data.PropertyIO;
import de.baane.wipe.view.FileMenu;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
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
		
		stage.setScene(scene);
		stage.show();
		
		INSTANCE = stage;
		initStage();
	}
	
	private VBox initGUI(Stage stage) {
		// Initialize content
		TableControlSwing c = new TableControlSwing();
		final SwingNode swingNode = new SwingNode();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				swingNode.setContent(c.askView());
			}
		});
		//FIXME: Full functions in FX
//		TableControl c = new TableControl();
//		TableViewFX swingNode = c.askView();
		
		c.checkSaveStatus();
		
		// Initialize menu
		FileMenu menuBar = new FileMenu(c);
		
		VBox pane = new VBox();
		pane.getChildren().add(menuBar);
		pane.getChildren().add(swingNode);
		
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
				stream.close();
				INSTANCE.getIcons().add(image);
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
