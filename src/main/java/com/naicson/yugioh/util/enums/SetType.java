package com.naicson.yugioh.util.enums;

import java.util.Arrays;

public enum SetType {
	
	BOX("BOX"),
	BOOSTER("BOOSTER"),
	DECK("DECK"),
	DEFAULT("DEFAULT"),
	TIN("TIN"),	
	USER_NEW_COLLECTION("COLLECTION"),
	UNKNOWN(null);
	
	private final String type;
	
	SetType(String typeOption) {
		this.type = typeOption;
	}
	
	public  String getType() {
		return type;
	}
	
	public static final SetType getByType(String value){
	    return Arrays.stream(SetType.values())
	    		.filter(type -> type.type.equals(value)).findFirst().orElse(UNKNOWN);
	}
	
}
