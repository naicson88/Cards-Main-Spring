package com.naicson.yugioh.mocks;

import java.util.Date;

import com.naicson.yugioh.entity.sets.DeckUsers;

public class DeckUsersMock {
	
	public static DeckUsers generateValidDeckUsers() {
		DeckUsers d = new DeckUsers();
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
