package com.naicson.yugioh.util.enums;

public enum GenericTypesCards {
		
	MONSTER(1, "assets/img/outras/monsterIcon.png"),
	FUSION(2, "assets/img/outras/fusionIcon.png"),
	RITUAL(3, "assets/img/outras/ritualIcon.png"),
	SYNCHRO(4, "assets/img/outras/syncronIcon.png"),
	XYZ(5, "assets/img/outras/xyzIcon.png"),
	PENDULUM(6, "assets/img/outras/pendulumIcon.png"),
	LINK(7, "assets/img/outras/linkIcon.png"),
	SPELL(8, "assets/img/outras/magicIcon.png"),
	TRAP(9,"assets/img/outras/trapIcon.png" ),
	TOKEN(10, "assets/img/outras/tokenicon.png"),
	SKILL(11, "assets/img/outras/SKILL.png");
	
	private int id;
	private String path;
	
	 GenericTypesCards(int id, String path) {
		this.id = id;
		this.path = path;
	}
	
	public int getId() {
		return id;
	}
	
	public String getPath() {
		return path;
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
