package com.naicson.yugioh.data.dto.set;

import java.util.Map;

import com.naicson.yugioh.util.enums.CardAttributes;

public class SetStatsDTO {
	
	private Map<CardAttributes, Integer> statsQuantityByAttribute;
	private Map<Integer, Integer> statsQuantityByLevel;
	private Map<String, Integer> statsQuantityByProperty;
	private Map<String, Integer> statsQuantityByGenericType;
	private Map<String, Integer> statsQuantityByType;
	private Map<Integer, Integer> statsAtk;
	private Map<Integer, Integer> statsDef;
	
	public Map<CardAttributes, Integer> getStatsQuantityByAttribute() {
		return statsQuantityByAttribute;
	}
	public void setStatsQuantityByAttribute(Map<CardAttributes, Integer> statsQuantityByAttribute) {
		this.statsQuantityByAttribute = statsQuantityByAttribute;
	}
	public Map<Integer, Integer> getStatsQuantityByLevel() {
		return statsQuantityByLevel;
	}
	public void setStatsQuantityByLevel(Map<Integer, Integer> statsQuantityByLevel) {
		this.statsQuantityByLevel = statsQuantityByLevel;
	}
	public Map<String, Integer> getStatsQuantityByProperty() {
		return statsQuantityByProperty;
	}
	public void setStatsQuantityByProperty(Map<String, Integer> statsQuantityByProperty) {
		this.statsQuantityByProperty = statsQuantityByProperty;
	}
	public Map<String, Integer> getStatsQuantityByGenericType() {
		return statsQuantityByGenericType;
	}
	public void setStatsQuantityByGenericType(Map<String, Integer> statsQuantityByGenericType) {
		this.statsQuantityByGenericType = statsQuantityByGenericType;
	}
	public Map<String, Integer> getStatsQuantityByType() {
		return statsQuantityByType;
	}
	public void setStatsQuantityByType(Map<String, Integer> statsQuantityByType) {
		this.statsQuantityByType = statsQuantityByType;
	}
	public Map<Integer, Integer> getStatsAtk() {
		return statsAtk;
	}
	public void setStatsAtk(Map<Integer, Integer> statsAtk) {
		this.statsAtk = statsAtk;
	}
	public Map<Integer, Integer> getStatsDef() {
		return statsDef;
	}
	public void setStatsDef(Map<Integer, Integer> statsDef) {
		this.statsDef = statsDef;
	}
	
	
}
