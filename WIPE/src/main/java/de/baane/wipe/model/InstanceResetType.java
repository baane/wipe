package de.baane.wipe.model;

import de.fhg.iml.vlog.ination.INation;

public enum InstanceResetType {
	WEEKLY,
	DAILY;
	
	private static final INation INATION = INation.openAndRegister(InstanceResetType.class);
	
	@Override
	public String toString() {
		return INATION.getTranslation(name());
	}
}
