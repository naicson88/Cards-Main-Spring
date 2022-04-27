package com.naicson.yugioh.service.setcollection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.naicson.yugioh.data.dto.set.DeckSummaryDTO;

public interface ISetsByType<T> {
	
 	Page<DeckSummaryDTO> findAllSetsByType(Pageable pageable, String setType);
}
