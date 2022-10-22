package com.naicson.yugioh.data.dto.set;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class SetCollectionDto {
	
	private String name;
	private String portugueseName;
	private String imgPath;
	private String requestSource;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date releaseDate;
	private String setType;
	private Boolean isSpeedDuel;
	private String setCode;

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPortugueseName() {
		return portugueseName;
	}
	public void setPortugueseName(String portugueseName) {
		this.portugueseName = portugueseName;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getSetType() {
		return setType;
	}
	public void setSetType(String setType) {
		this.setType = setType;
	}
	
	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}
	public boolean getIsSpeedDuel() {
		return isSpeedDuel;
	}
	public void setIsSpeedDuel(boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
	}
	
	public String getRequestSource() {
		return requestSource;
	}
	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}
	public String getSetCode() {
		return setCode;
	}
	public void setSetCode(String setCode) {
		this.setCode = setCode;
	}

}

