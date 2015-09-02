package de.baane.wipe.control;

import java.io.File;
import java.util.ArrayList;

import de.baane.wipe.control.data.DataIO;
import de.baane.wipe.control.data.PropertyIO;
import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import de.baane.wipe.model.DataHolder;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.InstanceResetType;
import de.baane.wipe.model.RaidStatus;
import de.fhg.iml.vlog.ination.INation;

public class TableControlBase {
	private static final INation INATION = INation.openAndRegister(TableControlBase.class); 
	
	protected static final boolean DEBUG = false;
	
	private DataHolder data;
	
	public TableControlBase() {
//		load(FileControl.SAVE_FILE);
	}
	
	public void load(File file) {
		if (file == null || !file.exists()) return;
		
		FileControl.SAVE_FILE = file;
		
		if (DEBUG) System.out.println("LOAD");
		data = DataIO.loadFromXMl(FileControl.SAVE_FILE);
		PropertyIO.saveToProperty(FileControl.WOW_LASTSAVEFILE, file.getAbsolutePath());
		if (data == null) createNewData();
		fillTable();
	}
	
	public void createNewData() {
		data = new DataHolder();
		initDefaultInstances();
		FileControl.isSaved = true;
	}
	private void initDefaultInstances() {
//		addInstance("Daily HC", InstanceResetType.DAILY);
		add1025Instances("AK");
		add1025Instances("Ony");
		add1025Instances("PDK");
		add1025Instances("PDOK");
		add1025Instances("ICC");
		addInstance("Weekly");
	}
	private void add1025Instances(String ini) {
		addInstance(ini + " 10");
		addInstance(ini + " 25");
	}
	
	public void checkSaveStatus() {
		if (FileControl.checkSaveStatus()) resetData();
	}
	
	public void fillTable() {}
	
	int findColumnNameIdx(String[] columnNames, String name) {
		for (int k = 0; k < columnNames.length; k++) {
			String colum = columnNames[k];
			if (colum.equals(name)) return k;
		}
		return -1;
	}

	public DataHolder getData() {
		return data;
	}
	
	public void addCharacter(String charName, CharacterClass charClass) {
		FileControl.isSaved = false;
		Character s = new Character(getMaxCharId(), charName, charClass);
		for (Instance i : data.getInstances())
			s.putProgress(i, RaidStatus.DEFAULT);
		data.getCharacters().add(s);
		
		fillTable();
	}
	
	private int getMaxCharId() {
		int maxId = 0;
		for (Character c : data.getCharacters())
			maxId = c.getId() > maxId ? c.getId() : maxId + 1;
		return maxId;
	}
	
	public void removeCharacter(Character c) {
		FileControl.isSaved = false;
		data.getCharacters().remove(c);
		fillTable();
	}
	
	public void addInstance(String iniName) {
		addInstance(iniName, InstanceResetType.WEEKLY);
	}
	public void addInstance(String iniName, InstanceResetType reset) {
		FileControl.isSaved = false;
		Instance instance = new Instance(getMaxIniId(), iniName, reset);
		data.getInstances().add(instance);
		
		for (Character c : data.getCharacters())
			c.putProgress(instance, RaidStatus.DEFAULT);
		
		fillTable();
	}
	
	private int getMaxIniId() {
		ArrayList<Instance> instances = data.getInstances();
		int maxId = instances.size();
		for (Instance i : instances) {
			if (i.getId() > maxId)
				maxId = i.getId() + 1;
		}
		return maxId + 1;
	}
	
	public void removeInstance(Instance ini) {
		FileControl.isSaved = false;
		data.getInstances().remove(ini);
		for (Character c : data.getCharacters())
			c.getProgresses().remove(ini.getId());
		fillTable();
	}
	
	public void resetData() {
		FileControl.isSaved = false;
		for (Character c : data.getCharacters()) {
			c.getProgresses().clear(); //TODO: check
		}
		
		fillTable();
	}
	
//	private TableModelListener getTableModelListener() {
//		return new TableModelListener() {
//			@Override
//			public void tableChanged(TableModelEvent e) {
//				int row = e.getFirstRow();
//				int column = e.getColumn();
//				TableModel model = (TableModel) e.getSource();
//				Character character = data.getCharacters().get(row);
//				
//				Object cellValue = model.getValueAt(row, column);
//				updateCell(column, character, cellValue);
//			}
//
//		};
//	}
	void updateCell(int column, Character character,
			Object cellValue) {
		// Character name cell
		if (column == 0) {
			if (cellValue instanceof String)
				character.setName((String) cellValue);
		}
		// Progress Cell
		if (cellValue instanceof RaidStatus) {
			RaidStatus status = (RaidStatus) cellValue;
			Instance i = data.getInstances().get(column - 1);
			character.getProgresses().put(i, status);
			if (DEBUG)
				System.out.println("NEWCHECK :: " 
						+ character.getName() + " | " 
						+ i.getName() + ": " + status);
		}
		
		FileControl.isSaved = false;
	}
	
	String localize(String text) {
		return INATION.getTranslation(text);
	}
}
