package de.baane.wipe.model;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Character implements Serializable {
	private static final long serialVersionUID = -1155029169158598164L;
	
	private int id;
	private String name;
	private CharacterClass charClass;
	
	private LinkedHashMap<Instance, RaidStatus> progresses;
	
	public Character(int id, String name, CharacterClass charClass) {
		this.id = id;
		this.name = name;
		this.charClass = charClass;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public CharacterClass getCharClass() {
		return charClass;
	}
	
	public void setCharClass(CharacterClass charClass) {
		this.charClass = charClass;
	}
	
	public LinkedHashMap<Instance, RaidStatus> getProgresses() {
		if (progresses == null)
			progresses = new LinkedHashMap<Instance, RaidStatus>();
		return progresses;
	}
	
	public void setProgresses(LinkedHashMap<Instance, RaidStatus> progresses) {
		this.progresses = progresses;
	}
	
	public void putProgress(Instance ini, RaidStatus progress) {
		getProgresses().put(ini, progress);
	}
	
	@Override
	public String toString() {
		return getName() + " (" + getCharClass() + ")";
	}
}
