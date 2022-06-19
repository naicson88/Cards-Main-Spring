package com.naicson.yugioh.mocks;

import com.naicson.yugioh.data.dto.RelUserDeckDTO;

public class RelUserDeckDTOMock {
	
	public static RelUserDeckDTO generateValidRelUserDeckDTO() {
		
		RelUserDeckDTO dto = new RelUserDeckDTO();
		dto.setDeckId(1);
		dto.setId(1);
		dto.setQuantity(10);
		dto.setUserId(1);
		
		return dto;
	}
}	
