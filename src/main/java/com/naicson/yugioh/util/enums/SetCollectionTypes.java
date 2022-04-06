package com.naicson.yugioh.util.enums;

public enum SetCollectionTypes {
	
	BOOSTER("Booster"),
	BOX("Box"),
	TIN("Tin");

	private final String setCollectionType;
	
	SetCollectionTypes(String setCollectionType){
		this.setCollectionType = setCollectionType;
	}
	
	public String getSetCollectionType() {
		return setCollectionType;
	}
}
