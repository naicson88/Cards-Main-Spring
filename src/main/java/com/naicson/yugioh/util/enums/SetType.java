package com.naicson.yugioh.util.enums;

public enum SetType {
	
	BOX("BOX"),
	BOOSTER("BOOSTER"),
	DECK("DECK"),
	DEFAULT("DEFAULT"),
	TIN("TIN");
	
	private final String type;
	
	SetType(String typeOption) {
		type = typeOption;
	}
	
	public String getType() {
		return type;
	}
}
