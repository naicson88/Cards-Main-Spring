package com.naicson.yugioh.util.enums;
import java.util.Arrays;

public enum CardProperty {
	
	CONTINUOUS("Continuous", "assets/img/outras/Continuous.png"),
	QUICK_PLAY("Quick-Play", "assets/img/outras/Quick.png"),
	EQUIP("Equip", "assets/img/outras/Equip.jpg"),
	NORMAL("Normal", ""),
	FIELD("Field","assets/img/outras/Field.png"),
	RITUAL("Ritual", "assets/img/outras/Ritual_Icon.png"),
	COUNTER("Counter", "assets/img/outras/Counter.png"),
	UNKNOWN("0", "");
	
	
	private final String property;
	private final String path;
	
	public String getCardProperty() {
		return property;
	}
	
	public String getPath() {
		return path;
	}
	
	CardProperty(String property, String path){
		this.property = property;
		this.path = path;
	}
	
	public static final CardProperty getByValue(String value){
	    return Arrays.stream(CardProperty.values())
	    		.filter(enumRole -> enumRole.property.equals(value)).findFirst()
	    		.orElseThrow(() -> new IllegalArgumentException("UNKNOWN Propertie: " + value));
	} 
}
