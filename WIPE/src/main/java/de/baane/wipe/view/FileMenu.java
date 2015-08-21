package de.baane.wipe.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import de.baane.wipe.MainFX;
import de.baane.wipe.control.FileControl;
import de.baane.wipe.control.TableControlBase;
import de.baane.wipe.control.WindowControl;
import de.baane.wipe.control.data.PropertyIO;
import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.Locales;
import de.baane.wipe.util.FileUtil;
import de.fhg.iml.vlog.ination.INation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.util.Pair;

public class FileMenu extends MenuBar {
	private static final INation INATION = INation.openAndRegister(FileMenu.class);
	
	class LoadAction implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			if(!FileControl.checkSaved(c.getData())) return;
			
			File file = FileControl.showLoadDialog(MainFX.INSTANCE);
			if (file == null) return;
			c.load(file);
		}
	}

	class SaveAction implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			FileControl.save(c.getData());
		}
	}

	class SaveAsAction implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent e) {
			FileControl.saveAs(c.getData());
		}
	}
	
	private TableControlBase c;
	
	public FileMenu(TableControlBase c) {
		this.c = c;
		init();
	}
	
	private void init() {
		this.getMenus().add(initFileMenu());
		this.getMenus().add(initAddDeleteMenu());
	}

	private Menu initFileMenu() {
		//File managing menu
		Menu menu = new Menu(localize("File"));
//		menu.setAccelerator(KeyCombination.keyCombination(KeyCombination.ALT_DOWN+""));
//		menu.getAccessibleContext().setAccessibleDescription("The menu");
//		menu.setMnemonicParsing(false);
		
		menu.getItems().add(initMenuItem(localize("New"), 
				KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN + "+ N"),
				"file_16",
				e -> {
					if (!FileControl.checkSaved(c.getData())) return;
					c.createNewData();
					c.fillTable();
				}
		));
		// Load & save
		menu.getItems().add(initMenuItem(localize("Load")+"...", 
				KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN + "+ O"),
				"open_16",
				new LoadAction()
		));
		
		menu.getItems().add(initMenuItem(localize("Save"), 
				KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN + "+ S"), 
				"save_16",
				new SaveAction()
		));
		
		menu.getItems().add(initMenuItem(localize("Save as")+"...", 
				new SaveAsAction()
		));
		
		menu.getItems().add(initMenuItem(localize("Manuell reset"), 
				KeyCombination.keyCombination(KeyCombination.ALT_DOWN + "+ R"),
				"bin_16",
				e ->  {
					if(!FileControl.checkSaved(c.getData())) return;
					c.resetData();
				}
		));
		
		// Separator
		menu.getItems().add(new SeparatorMenuItem());
		
		CheckMenuItem menuItemBox = new CheckMenuItem(localize("Tuesday warning"));
		menuItemBox.setOnAction(e -> {
				boolean selected = menuItemBox.isSelected();
				PropertyIO.saveToProperty(FileControl.WOW_WARN_TUESDAY, String.valueOf(selected));
				if (selected) FileUtil.tuesdayWarning();
		});
		loadIcon("reminder_16", menuItemBox);
		Boolean warnOn = Boolean.valueOf(PropertyIO.readProperty(FileControl.WOW_WARN_TUESDAY));
		menuItemBox.setSelected(warnOn);
		menu.getItems().add(menuItemBox);
		
		final String selectLanuageTitle = "select_language";
		menu.getItems().add(initMenuItem(localize(selectLanuageTitle), 
				null,
				"earth_16",
				e -> {
					// Setting default language
					//TODO: think about this
					Locales l = Locales.GERMAN;
					for (Locales l1 : Locales.values()) {
						if (l1.getLocale().equals(PropertyIO.LOCALE.getLocale()))
							l = l1;
					}
					
					 Locales locale = Message.showOption(
									 localize(selectLanuageTitle), 
									 localize("language"), 
									 l, 
									 Arrays.asList(Locales.values()));
					if (locale == null) return;
					
					PropertyIO.saveToProperty(PropertyIO.LANGUAGE, locale.name());
					PropertyIO.LOCALE = locale;
					Message.showWarning(localize("changes_after_restart"));
				}
		));
		
		// Separator
		menu.getItems().add(new SeparatorMenuItem());
		
		// EXIT MOTHERFUCKER!
		menu.getItems().add(initMenuItem(localize("Exit"),
				null,
				"exit",
				e -> {
					WindowControl.saveWindowPosition(MainFX.INSTANCE.getX(), MainFX.INSTANCE.getY());
					WindowControl.saveWindowSize(MainFX.INSTANCE.getWidth(), MainFX.INSTANCE.getHeight());
					if(FileControl.checkSaved(c.getData())) System.exit(0);
				}
		));
		
		return menu;
	}

	private Menu initAddDeleteMenu() {
		Menu menu = new Menu(localize("Edit"));
		
		menu.getItems().add(initMenuItem(localize("Add character"), 
				KeyCombination.keyCombination(KeyCombination.ALT_DOWN + "+ C"),
				"user",
				e -> {
					Pair<String, CharacterClass> charPair = new AddCharacterDialog(INATION).show();
					
					String charName = null;
					CharacterClass charClass = null;
					if (charPair != null) {
						charName = charPair.getKey();
						charClass = charPair.getValue();
					}
					if (charName == null || charClass == null) return;
					c.addCharacter(charName, charClass);
				}
		));
		
		menu.getItems().add(initMenuItem(localize("Add instance"), 
				KeyCombination.keyCombination(KeyCombination.ALT_DOWN + "+ I"),
				"map",
				e -> {
					// Without Reset Type
					String instanceName = Message.showInput(
							localize("Please insert instance informations"), 
							localize("instance_name"),
							"add");
					 if(instanceName == null) return;
					 c.addInstance(instanceName);
					
					// With Reset Type
//					Pair<String, InstanceResetType> iniPair = new AddInstanceDialog(INATION).show();
//					
//					String iniName = null;
//					InstanceResetType reset = null;
//					if (iniPair != null) {
//						iniName = iniPair.getKey();
//						reset = iniPair.getValue();
//					}
//					if (iniName == null || reset == null) return;
//					c.addInstance(iniName, reset);
				}
		));

		// Separator
		menu.getItems().add(new SeparatorMenuItem());
		
		// Delete
		menu.getItems().add(initMenuItem(localize("Remove character"), 
				e -> {
					ArrayList<Character> chars = c.getData().getCharacters();
					Character value = (Character) Message.showOption(
									localize("Remove character"),
									localize("character_name"), 
									chars.get(0), 
									Arrays.asList(chars.toArray()),
									"bin");
					
					if(value == null) return;
					c.removeCharacter(value);
				}
		));
		
		menu.getItems().add(initMenuItem(localize("Remove instance"), 
				e -> {
					ArrayList<Instance> instances = c.getData().getInstances();
					Instance value = (Instance) Message.showOption(
							localize("Remove instance"), 
							localize("instance_name"),
							instances.get(0), 
							Arrays.asList(instances.toArray()),
							"bin");
					
					if(value == null) return;
					c.removeInstance(value);
				}
		));
		
		return menu;
	}
	
	private MenuItem initMenuItem(String text, EventHandler<ActionEvent> action) {
		return initMenuItem(text, null, action);
	}
	
	private MenuItem initMenuItem(String text, KeyCombination keyStroke, EventHandler<ActionEvent> action) {
		return initMenuItem(text, keyStroke, null, action);
	}
	
	private MenuItem initMenuItem(String text, KeyCombination keyStroke, String iconName, EventHandler<ActionEvent> action) {
		MenuItem menuItem = new MenuItem(text);
		if (keyStroke != null) menuItem.setAccelerator(keyStroke);
		if (action != null) menuItem.setOnAction(action);
		if (iconName != null) loadIcon(iconName, menuItem);
		return menuItem;
	}
	
	private void loadIcon(String iconName, MenuItem menuItem) {
		try {
			if (!iconName.endsWith(".png")) iconName += ".png";
			String url = Message.class.getResource("icons/" + iconName).toString();
			Image img = new Image(url, 16, 16, false, false); 
			ImageView imageView = new ImageView(img);
			menuItem.setGraphic(imageView);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String localize(String text) {
		return INATION.getTranslation(text);
	}
	
}
