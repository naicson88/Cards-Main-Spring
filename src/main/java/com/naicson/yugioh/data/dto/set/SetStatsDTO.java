package com.naicson.yugioh.data.dto.set;

import java.util.List;
import java.util.Map;

import com.naicson.yugioh.data.dto.GenericTypeDTO;
import com.naicson.yugioh.entity.Atributo;
import com.naicson.yugioh.entity.TipoCard;
import com.naicson.yugioh.util.enums.CardAttributes;

public class SetStatsDTO {
	
	private Map<Integer, Integer> statsQuantityByLevel;
	private Map<String, Integer> statsQuantityByProperty;
	private Map<String, Integer> statsQuantityByGenericType;
	private Map<String, Integer> statsQuantityByType;
	private Map<Integer, Integer> statsAtk;
	private Map<Integer, Integer> statsDef;
	private List<Atributo> atributos;
	private List<GenericTypeDTO> genericTypes;
	private List<TipoCard> tipoCard;
	private List<Map<String, Integer>> listAtk;
	private List<Map<String, Integer>> listDef;
	
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
	public List<Atributo> getAtributos() {
		return atributos;
	}
	public void setAtributos(List<Atributo> atributos) {
		this.atributos = atributos;
	}
	public List<GenericTypeDTO> getGenericTypes() {
		return genericTypes;
	}
	public void setGenericTypes(List<GenericTypeDTO> genericTypes) {
		this.genericTypes = genericTypes;
	}
	public List<TipoCard> getTipoCard() {
		return tipoCard;
	}
	public void setTipoCard(List<TipoCard> tipoCard) {
		this.tipoCard = tipoCard;
	}
	public List<Map<String, Integer>> getListAtk() {
		return listAtk;
	}
	public void setListAtk(List<Map<String, Integer>> listAtk) {
		this.listAtk = listAtk;
	}
	public List<Map<String, Integer>> getListDef() {
		return listDef;
	}
	public void setListDef(List<Map<String, Integer>> listDef) {
		this.listDef = listDef;
	}
	
	
}
