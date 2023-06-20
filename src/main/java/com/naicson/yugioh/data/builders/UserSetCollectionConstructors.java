package com.naicson.yugioh.data.builders;

import java.util.Date;
import java.util.List;

import com.naicson.yugioh.data.dto.set.UserSetCollectionDTO;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.SetType;

public class UserSetCollectionConstructors {
	
	public static void convertToUserSetCollection(IUserSetCollectionBuilder builder, SetCollection set) {
		builder.setId(null);
		builder.setDtUpdate(new Date());
		builder.setName(set.getName()+"_"+GeneralFunctions.randomUniqueValue());
		builder.setRegistrationDate(new Date());
		builder.setUserId(GeneralFunctions.userLogged().getId());
		builder.setImgPath(set.getImgPath());
		builder.setImgurUrl(set.getImgPath());
		builder.setKonamiSetCopied(set.getId());
		builder.setReleaseDate(set.getReleaseDate());
		builder.setIsSpeedDuel(set.getIsSpeedDuel());
		builder.setSetCollectionType(set.getSetCollectionType());
		UserDeck ud = UserDeck.userDeckFromDeck(set.getDecks().get(0));		
		ud.setNome(set.getName()+"_"+GeneralFunctions.randomUniqueValue());
		ud.setId(null);
		ud.setKonamiDeckCopied(set.getDecks().get(0).getId());
		ud.setSetType(set.getSetCollectionType().toString());
		builder.setUserDeck(List.of(ud));	
	}
	
	public static void convertFromUserSetCollectionDTO(IUserSetCollectionBuilder builder, UserSetCollectionDTO userCollection, UserDeck userDeck) {
		builder.setDtUpdate(new Date());
		builder.setImgPath(GeneralFunctions.getRandomCollectionCase());
		builder.setIsSpeedDuel(false);
		builder.setName(userCollection.getName());
		builder.setOnlyDefaultDeck(true);
		builder.setRegistrationDate(new Date());
		builder.setReleaseDate(new Date());
		builder.setSetCollectionType(SetType.USER_NEW_COLLECTION);
		builder.setUserDeck(List.of(userDeck));
		builder.setUserId(GeneralFunctions.userLogged().getId());
		
	}
}
