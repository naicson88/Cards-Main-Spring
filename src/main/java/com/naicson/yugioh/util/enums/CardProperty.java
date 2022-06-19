package com.naicson.yugioh.util.enums;
import java.util.Arrays;

public enum CardProperty {
	
	CONTINUOUS("Continuous"),
	QUICK_PLAY("Quick-Play"),
	EQUIP("Equip"),
	NORMAL("Normal"),
	FIELD("Field"),
	RITUAL("Ritual"),
	COUNTER("Counter"),
	UNKNOWN("0");
	
	
	private final String property;
	
	public String getCardProperty() {
		return property;
	}
	
	CardProperty(String property){
		this.property = property;
	}
	
	public static final CardProperty getByValue(String value){
	    return Arrays.stream(CardProperty.values())
	    		.filter(enumRole -> enumRole.property.equals(value)).findFirst().orElse(UNKNOWN);
	}

}
