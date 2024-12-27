package com.naicson.yugioh.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "tab_tipo_card")
public class TipoCard {
	
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String tipoCardImgPath;
	@Transient
	private int quantity;

	public TipoCard(Long id, String name, String tipoCardImgPath, int quantity) {
		this.id = id;
		this.name = name;
		this.tipoCardImgPath = tipoCardImgPath;
		this.quantity = quantity;
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
	public String getTipoCardImgPath() {
		return tipoCardImgPath;
	}
	public void setTipoCardImgPath(String tipoCardImgPath) {
		this.tipoCardImgPath = tipoCardImgPath;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoCard other = (TipoCard) obj;
		return Objects.equals(id, other.id);
	}
	
	
	
}
