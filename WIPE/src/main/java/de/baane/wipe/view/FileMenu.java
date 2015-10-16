package de.baane.wipe.view;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import de.baane.wipe.MainFX;
import de.baane.wipe.control.FileControl;
import de.baane.wipe.control.TableControl;
import de.baane.wipe.control.WindowControl;
import de.baane.wipe.control.data.PropertyIO;
import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.Locales;
import de.baane.wipe.util.FileUtil;
import de.fhg.iml.vlog.ination.INation;
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
	
	interface IMenuItem {
		String getIconName();
		String getKeyStroke();
		abstract void getAction();
	}
	
	private abstract class MenuAction extends MenuItem implements IMenuItem {
		public MenuAction(String text) {
			super(text);
			setOnAction(event -> getAction());
			if (getKeyStroke() != null) setAccelerator(KeyCombination.keyCombination(getKeyStroke()));
			if (getIconName() != null) setGraphic(loadIcon(getIconName()));
		}
		
		@Override
		public String getIconName() {
			return null;
		}
		
		@Override
		public String getKeyStroke() {
			return null;
		}
	}
	
	private abstract class CheckMenuAction extends CheckMenuItem implements IMenuItem {
		public CheckMenuAction(String text) {
			super(text);
			setOnAction(event -> getAction());
			if (getKeyStroke() != null) setAccelerator(KeyCombination.keyCombination(getKeyStroke()));
			if (getIconName() != null) setGraphic(loadIcon(getIconName()));
		}
		
		@Override
		public String getIconName() {
			return null;
		}
		
		@Override
		public String getKeyStroke() {
			return null;
		}
	}
	
	private class TuesdayWarningCheckAction extends CheckMenuAction {
		public TuesdayWarningCheckAction() {
			super(localize("Tuesday warning"));
			setSelected(Boolean.valueOf(PropertyIO.readProperty(FileControl.WOW_WARN_TUESDAY)));
		}
		
		@Override
		public String getIconName() {
			return "reminder_16";
		}
		
		@Override
		public void getAction() {
			PropertyIO.saveToProperty(FileControl.WOW_WARN_TUESDAY, String.valueOf(isSelected()));
			if (isSelected()) FileUtil.tuesdayWarning();
		}
	}
	
	private class NewMenuAction extends MenuAction {
		public NewMenuAction() {
			super(localize("New"));
		}
		
		@Override
		public String getIconName() {
			return "file_16";
		}
		
		@Override
		public String getKeyStroke() {
			return KeyCombination.CONTROL_DOWN + "+ N";
		}
		
		@Override
		public void getAction() {
			if (!FileControl.checkSaved(c.getData())) return;
			c.createNewData();
			c.fillTable();
		}
	}
	
	private class LoadMenuAction extends MenuAction {
		public LoadMenuAction() {
			super(localize("Load") + "...");
		}
		
		@Override
		public String getIconName() {
			return "open_16";
		}
		
		@Override
		public String getKeyStroke() {
			return KeyCombination.SHORTCUT_DOWN + "+ O";
		}
		
		@Override
		public void getAction() {
			if (!FileControl.checkSaved(c.getData())) return;
			
			File file = FileControl.showLoadDialog(MainFX.INSTANCE);
			if (file == null) return;
			c.load(file);
		}
	}
	
	private class SaveAction extends MenuAction {
		public SaveAction() {
			super(localize("Save"));
		}
		
		@Override
		public String getIconName() {
			return "save_16";
		}
		
		@Override
		public String getKeyStroke() {
			return KeyCombination.SHORTCUT_DOWN + "+ S";
		}
		
		@Override
		public void getAction() {
			FileControl.save(c.getData());
		}
	}
	
	private class SaveAsAction extends MenuAction {
		public SaveAsAction() {
			super(localize("Save as") + "...");
		}
		
		@Override
		public void getAction() {
			FileControl.saveAs(c.getData());
		}
	}
	
	private class ManuellResetAction extends MenuAction {
		public ManuellResetAction() {
			super(localize("Manuell reset"));
		}
		
		@Override
		public String getIconName() {
			return "bin_16";
		}
		
		@Override
		public String getKeyStroke() {
			return KeyCombination.ALT_DOWN + "+ R";
		}
		
		@Override
		public void getAction() {
			if (!FileControl.checkSaved(c.getData())) return;
			c.resetData();
		}
	}
	
	private class SelectLanguageAction extends MenuAction {
		public SelectLanguageAction() {
			super(localize("select_language"));
		}
		
		@Override
		public String getIconName() {
			return "earth_16";
		}
		
		@Override
		public void getAction() {
			Locales locale = new ChooseLanguageDialog(INATION).show();
			if (locale == null) return;
			
			PropertyIO.saveToProperty(PropertyIO.LANGUAGE, locale.name());
			PropertyIO.LOCALE = locale;
			Message.showWarning(localize("changes_after_restart"));
		}
		
	}
	
	private class ExitAction extends MenuAction {
		public ExitAction() {
			super(localize("Exit"));
		}
		
		@Override
		public String getIconName() {
			return "exit";
		}
		
		@Override
		public void getAction() {
			WindowControl.saveWindowPosition(MainFX.INSTANCE.getX(), MainFX.INSTANCE.getY());
			WindowControl.saveWindowSize(MainFX.INSTANCE.getWidth(), MainFX.INSTANCE.getHeight());
			if (FileControl.checkSaved(c.getData())) System.exit(0);
		}
	}
	
	private class AddCharacterAction extends MenuAction {
		public AddCharacterAction() {
			super(localize("Add character"));
		}
		
		@Override
		public String getIconName() {
			return "user";
		}
		
		@Override
		public String getKeyStroke() {
			return KeyCombination.ALT_DOWN + "+ C";
		}
		
		@Override
		public void getAction() {
			Pair<String, CharacterClass> charPair = new AddCharacterDialog(INATION).show();
			if (charPair == null) return;
			
			String charName = charPair.getKey();
			CharacterClass charClass = charPair.getValue();
			if (charName == null || charClass == null) return;
			c.addCharacter(charName, charClass);
		}
	}
	
	private class AddInstanceAction extends MenuAction {
		public AddInstanceAction() {
			super(localize("Add instance"));
		}
		
		@Override
		public String getIconName() {
			return "map";
		}
		
		@Override
		public String getKeyStroke() {
			return KeyCombination.ALT_DOWN + "+ I";
		}
		
		@Override
		public void getAction() {
			// Without Reset Type
			String instanceName = Message.showInput(
					localize("Please insert instance informations"), 
					localize("instance_name"),
					"add");
			if (instanceName == null) return;
			c.addInstance(instanceName);
			
			// TODO: Use and check this if daily reset in implemented
//			Pair<String, InstanceResetType> iniPair = new AddInstanceDialog(INATION).show();
//			
//			String iniName = null;
//			InstanceResetType reset = null;
//			if (iniPair != null) {
//				iniName = iniPair.getKey();
//				reset = iniPair.getValue();
//			}
//			if (iniName == null || reset == null) return;
//			c.addInstance(iniName, reset);
		}
	}
	
	private class RemoveCharacterAction extends MenuAction {
		public RemoveCharacterAction() {
			super(localize("Remove character"));
		}
		
		@Override
		public void getAction() {
			ArrayList<Character> chars = c.getData().getCharacters();
			Character value = new RemoveCharacterDialog(INATION).show(chars);
			if (value == null) return;
			c.removeCharacter(value);
		}
	}
	
	private class RemoveInstanceAction extends MenuAction {
		public RemoveInstanceAction() {
			super(localize("Remove instance"));
		}
		
		@Override
		public void getAction() {
			ArrayList<Instance> instances = c.getData().getInstances();
			Instance value = (Instance) Message.showOption(
					localize("Remove instance"), 
					localize("instance_name"),
					instances.get(0), 
					Arrays.asList(instances.toArray()),
					"bin");
			
			if (value == null) return;
			c.removeInstance(value);
		}
	}
	
	private TableControl c;
	
	public FileMenu(TableControl c) {
		this.c = c;
		initMenu();
	}
	
	private void initMenu() {
		this.getMenus().add(initFileMenu());
		this.getMenus().add(initAddDeleteMenu());
	}
	
	private Menu initFileMenu() {
		// File managing menu
		Menu menu = new Menu(localize("File"));
		
		menu.getItems().add(new NewMenuAction());
		// Load & save
		menu.getItems().add(new LoadMenuAction());
		menu.getItems().add(new SaveAction());
		menu.getItems().add(new SaveAsAction());
		
		menu.getItems().add(new ManuellResetAction());
		
		// Separator
		menu.getItems().add(new SeparatorMenuItem());
		
		// WIPE options
		menu.getItems().add(new TuesdayWarningCheckAction());
		menu.getItems().add(new SelectLanguageAction());
		
		// Separator
		menu.getItems().add(new SeparatorMenuItem());
		
		// EXIT MOTHERFUCKER!
		menu.getItems().add(new ExitAction());
		
		return menu;
	}
	
	private Menu initAddDeleteMenu() {
		Menu menu = new Menu(localize("Edit"));
		
		// Add
		menu.getItems().add(new AddCharacterAction());
		menu.getItems().add(new AddInstanceAction());
		
		// Separator
		menu.getItems().add(new SeparatorMenuItem());
		
		// Delete
		menu.getItems().add(new RemoveCharacterAction());
		menu.getItems().add(new RemoveInstanceAction());
		
		return menu;
	}
	
	private ImageView loadIcon(String iconName) {
		if (!iconName.endsWith(".png")) iconName += ".png";
		String iconPath = "icons/" + iconName;
		InputStream stream = null;
		try {
			stream = FileMenu.class.getResourceAsStream(iconPath);
			return new ImageView(new Image(stream, 16, 16, false, false));
		} catch (Exception e) {
			System.err.println("Icon \"" + iconName + "\" not found in " + iconPath);
			e.printStackTrace();
		} finally {
			if (stream != null) try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private String localize(String text) {
		return INATION.getTranslation(text);
	}
	
}
