package com.naicson.yugioh.data.dto.set;

public class DeckAndSetsBySetTypeDTO {
	
	private Long setId;
	private String name;

	
	public DeckAndSetsBySetTypeDTO() {}
	
	public DeckAndSetsBySetTypeDTO(Long setId, String name) {
		super();
		this.setId = setId;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getSetId() {
		return setId;
	}
	public void setSetId(Long setId) {
		this.setId = setId;
	}
	
	
}
