package com.naicson.yugioh.util.enums;

public enum GenericTypesCards {
		
	MONSTER(1),
	FUSION(2),
	RITUAL(3),
	SYNCHRO(4),
	XYZ(5),
	PENDULUM(6),
	LINK(7),
	SPELL(8),
	TRAP(9),
	TOKEN(10);
	
	private int id;
	
	 GenericTypesCards(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static GenericTypesCards getFromId(int id) {
		for(GenericTypesCards gt: GenericTypesCards.values()) {
			if(gt.getId() == id) {
				return gt;
			}
		}
		
		return null;
	}
	
	public static int getOrderByGenerycType(String enumGenericType) {
		return GenericTypesCards.valueOf(enumGenericType).getId();
	}
}
