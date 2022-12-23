package com.naicson.yugioh.mocks;

import java.util.List;

import com.naicson.yugioh.data.dto.set.AssociationDTO;

public class AssociationDTOMock {
	
	public static AssociationDTO returnAssociationDTO() {
		AssociationDTO dto = new AssociationDTO();
		dto.setSourceId(1);
		dto.setArrayToAssociate(List.of(654, 358));
		
		return dto;
	}
}
