package com.naicson.yugioh.data.builders;

import java.util.Date;
import java.util.List;

import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.util.enums.SetType;

public interface IUserSetCollectionBuilder {
	
	public void setId(Long id);

	public void setUserId(Long userId);

	public void setName(String name);

	public void setPortugueseName(String portugueseName);

	public void setImgPath(String imgPath);

	public void setOnlyDefaultDeck(Boolean onlyDefaultDeck);

	public void setReleaseDate(Date releaseDate);

	public void setRegistrationDate(Date registrationDate);

	public void setIsSpeedDuel(Boolean isSpeedDuel);

	public void setImgurUrl(String imgurUrl);

	public void setDtUpdate(Date dtUpdate);

	public void setKonamiSetCopied(Integer konamiSetCopied);

	public void setUserDeck(List<UserDeck> userDeck);

	public void setSetCollectionType(SetType setCollectionType);
	
		
}
