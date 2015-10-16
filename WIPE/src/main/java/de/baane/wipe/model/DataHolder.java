package de.baane.wipe.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class DataHolder implements Serializable {
	private static final long serialVersionUID = 3858670694200394664L;
	
	private ArrayList<Character> characters;
	private ArrayList<Instance> instances;
	
	public ArrayList<Character> getCharacters() {
		if (characters == null) characters = new ArrayList<Character>();
		return characters;
	}
	
	public ArrayList<Instance> getInstances() {
		if (instances == null) instances = new ArrayList<Instance>();
		return instances;
	}
	
	public void addCharacter(Character c) {
		getCharacters().add(c);
	}
	
	public void addInstance(Instance ini) {
		getInstances().add(ini);
		for (Character s : getCharacters()) {
			LinkedHashMap<Instance, RaidStatus> progresses = s.getProgresses();
			if (!progresses.containsKey(ini))
				s.getProgresses().put(ini, RaidStatus.DEFAULT);
		}
	}
	
	public Character findCharacter(int id) {
		for (Character c : getCharacters()) if (c.getId() == id) return c;
		return null;
	}
	
	public Instance findInstance(int id) {
		for (Instance i : getInstances()) if (i.getId() == id) return i;
		return null;
	}
}
