package com.naicson.yugioh.data.dto.set;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public class AssociationDTO {
	
	@NotEmpty(message =  "Source ID cannot be empty")
	private Integer sourceId;
	@NotEmpty(message =  "Array to Associate cannot be empty")
	private List<Integer> arrayToAssociate;
	
	public Integer getSourceId() {
		return sourceId;
	}
	
	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}
	
	public List<Integer> getArrayToAssociate() {
		return arrayToAssociate;
	}
	
	public void setArrayToAssociate(List<Integer> arrayToAssociate) {
		this.arrayToAssociate = arrayToAssociate;
	}

	@Override
	public String toString() {
		return "AssociationDTO [sourceId=" + sourceId + ", arrayToAssociate=" + arrayToAssociate + "]";
	}
	
	
	
}
