package com.naicson.yugioh.service.setcollection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.util.enums.SetType;

public interface ISetsByType<T> {
	
 	Page<Deck> findAllSetsByType(Pageable pageable, SetType setType);
}
