package com.naicson.yugioh.data.dto;

public class CollectionDeck extends KonamiDeck{
	
	private static final long serialVersionUID = 1L;
	private Integer setId;
	private String filterSetCode;
	
	public Integer getSetId() {
		return setId;
	}
	public void setSetId(Integer setId) {
		this.setId = setId;
	}
	public String getFilterSetCode() {
		return filterSetCode;
	}
	public void setFilterSetCode(String filterSetCode) {
		this.filterSetCode = filterSetCode;
	}
}
