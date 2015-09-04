package de.baane.wipe.control;

import java.util.ArrayList;

import de.baane.wipe.model.Character;
import de.baane.wipe.model.Instance;
import de.baane.wipe.view.table.TableViewFX;

public class TableControl extends TableControlBase {
	
	private TableViewFX view;
	
	public TableControl() {
		super();
	}
	
	@Override
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
			if(DEBUG) System.out.println("Filltable (INI) ::  "+i.getName());
			j++;
			columnNames[j] = i.getName();
		}
		return columnNames;
	}
	
}
