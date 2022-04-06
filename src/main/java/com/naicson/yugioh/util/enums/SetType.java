package com.naicson.yugioh.util.enums;

public enum SetType {
	
	BOX("B"),
	BOOSTER("BST"),
	DECK("D"),
	DEFAULT("DEF"),
	TIN("T");
	
	private final String type;
	
	SetType(String typeOption) {
		type = typeOption;
	}
	
	public String getType() {
		return type;
	}
}
