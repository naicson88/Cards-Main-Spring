package com.naicson.yugioh.mocks;

import java.util.Date;

import com.naicson.yugioh.entity.sets.UserDeck;


public class UserDeckMock {
	
	public static UserDeck generateValidUserDeck() {
		UserDeck d = new UserDeck();
		d.setDtCriacao(new Date());
		d.setId(1L);
		d.setImagem("Any String");
		d.setKonamiDeckCopied(null);
		d.setNome("Any Deck Name");
		d.setSetType("D");
		d.setUserId(1);
		
		return d;
	}
}
