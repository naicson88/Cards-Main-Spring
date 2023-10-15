package com.naicson.yugioh.mocks;

import cardscommons.dto.AssociationDTO;

import java.util.List;

public class AssociationDTOMock {
	
	public static AssociationDTO returnAssociationDTO() {
		AssociationDTO dto = new AssociationDTO();
		dto.setSourceId(1);
		dto.setArrayToAssociate(List.of(654, 358));
		
		return dto;
	}
}
