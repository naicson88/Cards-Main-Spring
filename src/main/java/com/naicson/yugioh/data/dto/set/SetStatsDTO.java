package com.naicson.yugioh.data.dto.set;

import java.util.List;
import java.util.Map;

import com.naicson.yugioh.data.dto.GenericTypeDTO;
import com.naicson.yugioh.entity.Atributo;
import com.naicson.yugioh.entity.TipoCard;
import com.naicson.yugioh.util.enums.CardAttributes;

public class SetStatsDTO {

	private List<Atributo> atributos;
	private List<GenericTypeDTO> genericTypes;
	private List<TipoCard> tipoCard;
	private List<Map<String, Integer>> listAtk;
	private List<Map<String, Integer>> listDef;
	private List<Map<String, Object>> listLevel;
	private List<Map<String, Object>> listProperties;
	private List<Map<String, Object>> listMostValuable;
	
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
	public List<Map<String, Object>> getListLevel() {
		return listLevel;
	}
	public void setListLevel(List<Map<String, Object>> listLevel) {
		this.listLevel = listLevel;
	}
	public List<Map<String, Object>> getListProperties() {
		return listProperties;
	}
	public void setListProperties(List<Map<String, Object>> listProperties) {
		this.listProperties = listProperties;
	}
	public List<Map<String, Object>> getListMostValuable() {
		return listMostValuable;
	}
	public void setListMostValuable(List<Map<String, Object>> listMostValuable) {
		this.listMostValuable = listMostValuable;
	}
	
	
}
