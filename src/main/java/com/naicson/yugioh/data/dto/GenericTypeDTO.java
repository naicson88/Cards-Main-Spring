package com.naicson.yugioh.data.dto;

public class GenericTypeDTO {
	
	private String name;
	private String path;
	private int quantity;
	
	public GenericTypeDTO() {}
	
	public GenericTypeDTO(String name, String path, int quantity) {
		super();
		this.name = name;
		this.path = path;
		this.quantity = quantity;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
}
