package de.baane.wipe.control;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import de.baane.wipe.control.data.DataIO;
import de.baane.wipe.control.data.PropertyIO;
import de.baane.wipe.model.Character;
import de.baane.wipe.model.CharacterClass;
import de.baane.wipe.model.DataHolder;
import de.baane.wipe.model.Instance;
import de.baane.wipe.model.InstanceResetType;
import de.baane.wipe.model.RaidStatus;
import de.baane.wipe.view.table.TableViewFX;
import de.fhg.iml.vlog.ination.INation;

public class TableControl {
	private static final INation INATION = INation.openAndRegister(TableControl.class); 
	
	protected static final boolean DEBUG = false;
	
	private TableViewFX view;
	private DataHolder data;
	
	public TableControl() {
		load(FileControl.SAVE_FILE);
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
		for (Character c : data.getCharacters())
			c.getProgresses().clear();
		
		fillTable();
	}
	
	//TODO: Do not use this every time. Use Observable?
	public void fillTable() {
		ArrayList<Character> characters = getData().getCharacters();
		ArrayList<Instance> instances = getData().getInstances();
		
		updateProgresses(characters, instances);
		
		askView().setModel(characters, readColumnNames(instances));
	}
	
	public TableViewFX askView() {
		if (view == null) view = new TableViewFX();
		return view;
	}
	
	/**
	 * Reading column names by given instances.
	 * 
	 * @param instances
	 * @return Array with instance names.
	 */
	private String[] readColumnNames(ArrayList<Instance> instances) {
		String[] columnNames = new String[instances.size()+1];
		int j = 0;
		columnNames[j] = localize("Character");
		for (Instance i : instances) {
			if (DEBUG) System.out.println("Filltable (INI) ::  "+i.getName());
			j++;
			columnNames[j] = i.getName();
		}
		return columnNames;
	}
	
	/**
	 * Refill all instances for each character with the default value.
	 */
	protected void updateProgresses(ArrayList<Character> characters, ArrayList<Instance> instances) {
		for (Character c : characters) {
			if(DEBUG) System.out.println("Filltable (CHAR) :: "+c.getName());
			LinkedHashMap<Instance, RaidStatus> progress = c.getProgresses();
			
			A: for (Instance ini : instances) {
				for (Entry<Instance, RaidStatus> e : progress.entrySet()) {
					if (ini.equals(e.getKey())) continue A;
				}
				progress.put(ini, RaidStatus.DEFAULT);
			}
		}
	}
	
	String localize(String text) {
		return INATION.getTranslation(text);
	}
}
