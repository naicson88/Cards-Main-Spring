package com.naicson.yugioh.util.enums;

public enum CardAttributes {
	WATER("Water", "assets/img/outras/WATER.png"),
	FIRE("Fire", "assets/img/outras/FIRE.png"),
	EARTH("Earth", "assets/img/outras/TERRA.png"),
	LIGHT("Light", "assets/img/outras/LUZ.png"),
	DARK("Dark", "assets/img/outras/DARK.png"),
	WIND("Wind", "assets/img/outras/WIND.png"),
	SPELL("Spell","assets/img/outras/MAGIA.png" ),
	TRAP("Trap", "assets/img/outras/ARMADILHA.png"),
	DIVINE("Divine", "assets/img/outras/LUZ.png"),
	SKILL("Skill", "assets/img/outras/SKILL.png");
	

	private final String name;
	private final String path;
	
	CardAttributes(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}
}
