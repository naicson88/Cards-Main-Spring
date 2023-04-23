package com.naicson.yugioh.data.builders;

import java.util.Date;
import java.util.List;

import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.util.enums.SetType;

public class UserSetCollectionBuilder implements IUserSetCollectionBuilder {
	private Long id;
	private Long userId;
	private String name;
	private String portugueseName;
	private String imgPath;
	private Boolean onlyDefaultDeck;
	private Date releaseDate;
	private Date registrationDate;
	private Boolean isSpeedDuel;
	private String imgurUrl;
	private Date dtUpdate;
	private Integer konamiSetCopied;
	private List<UserDeck> userDeck;
	private SetType setCollectionType;
	
	@Override
	public void setId(Long id) {
		this.id = id;
		
	}

	@Override
	public void setUserId(Long userId) {
		this.userId = userId;
		
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}

	@Override
	public void setPortugueseName(String portugueseName) {
		this.portugueseName = portugueseName;
		
	}

	@Override
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
		
	}

	@Override
	public void setOnlyDefaultDeck(Boolean onlyDefaultDeck) {
		this.onlyDefaultDeck = onlyDefaultDeck;
		
	}

	@Override
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
		
	}

	@Override
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
		
	}

	@Override
	public void setIsSpeedDuel(Boolean isSpeedDuel) {
		this.isSpeedDuel = isSpeedDuel;
		
	}

	@Override
	public void setImgurUrl(String imgurUrl) {
		this.imgurUrl = imgurUrl;
		
	}

	@Override
	public void setDtUpdate(Date dtUpdate) {
		this.dtUpdate = dtUpdate;
		
	}

	@Override
	public void setKonamiSetCopied(Integer konamiSetCopied) {
		this.konamiSetCopied = konamiSetCopied;
		
	}

	@Override
	public void setUserDeck(List<UserDeck> userDeck) {
		this.userDeck = userDeck;
		
	}

	@Override
	public void setSetCollectionType(SetType setCollectionType) {
		this.setCollectionType = setCollectionType;
		
	}
	
	public UserSetCollection getResult() {
		return new UserSetCollection(id, userId, name, portugueseName, imgPath, onlyDefaultDeck, 
				releaseDate, registrationDate, isSpeedDuel, imgurUrl, dtUpdate, konamiSetCopied, userDeck, setCollectionType);
	}

}
