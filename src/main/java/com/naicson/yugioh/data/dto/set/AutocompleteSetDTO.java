package com.naicson.yugioh.data.dto.set;

import java.math.BigInteger;

public class AutocompleteSetDTO {
	
	private BigInteger setId;
	private String setName;
	
	
	
	public AutocompleteSetDTO(BigInteger setId, String setName) {
		super();
		this.setId = setId;
		this.setName = setName;
	}
	
	public BigInteger getSetId() {
		return setId;
	}
	public void setSetId(BigInteger setId) {
		this.setId = setId;
	}
	public String getSetName() {
		return setName;
	}
	public void setSetName(String setName) {
		this.setName = setName;
	}
	
	
}
