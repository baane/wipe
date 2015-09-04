package de.baane.wipe.model;

import java.io.Serializable;

public class Instance implements Serializable {
	private static final long serialVersionUID = -228102269478925363L;
	
	private int id;
	private String name;
	private InstanceResetType reset = InstanceResetType.WEEKLY;
	
	public Instance(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Instance(int id, String name, InstanceResetType reset) {
		this.id = id;
		this.name = name;
		this.reset = reset;
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
	
	public InstanceResetType getReset() {
		return reset;
	}

	public void setReset(InstanceResetType reset) {
		this.reset = reset;
	}

	@Override
	public String toString() {
		return getName() + " (" + getReset() + ")";
	}
}
