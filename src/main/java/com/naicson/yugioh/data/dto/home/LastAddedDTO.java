package com.naicson.yugioh.data.dto.home;

import com.naicson.yugioh.util.enums.SetType;

import java.util.Date;

public class LastAddedDTO {
	
	private Long id;
	private String name;
	private String setCode;
	private Double price;
	private String img;
	private Long cardNumber;
	private Date registeredDate;
	private SetType setType;

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

	public String getSetCode() {
		return setCode;
	}

	public void setSetCode(String setCode) {
		this.setCode = setCode;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Long getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(Long cardNumber) {
		this.cardNumber = cardNumber;
	}



	public Date getRegisteredDate() {
		return registeredDate;
	}



	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}

	public SetType getSetType() {
		return setType;
	}

	public void setSetType(SetType setType) {
		this.setType = setType;
	}
	
	
}
