package com.naicson.yugioh.data.dto.home;

public class GeneralSearchDTO {
	
	private Long id;
	private String name;
	private String entityType;
	private String img;
	private String setCode;
		
	
	
	public GeneralSearchDTO(Long id, String name, String entityType, String img, String setCode) {
		super();
		this.id = id;
		this.name = name;
		this.entityType = entityType;
		this.img = img;
		this.setCode = setCode;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}

	public String getSetCode() {
		return setCode;
	}

	public void setSetCode(String setCode) {
		this.setCode = setCode;
	}
	
	
}
