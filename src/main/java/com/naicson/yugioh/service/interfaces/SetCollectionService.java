package com.naicson.yugioh.service.interfaces;

import java.util.List;

import javax.validation.Valid;

import com.naicson.yugioh.data.dto.set.AssociationDTO;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.data.dto.set.SetEditDTO;
import com.naicson.yugioh.entity.sets.SetCollection;

public interface SetCollectionService {
	
	public SetCollection findById(Integer id);
	
	public SetCollection saveSetCollection(SetCollection setCollection)  throws Exception;
	
//	public SetDetailsDTO setCollectionDetailsAsDeck(Long setId, String source);

	public List<DeckAndSetsBySetTypeDTO> getAllSetsBySetType(String setType);

	public AssociationDTO newAssociation(@Valid AssociationDTO dto);

	public SetEditDTO editCollection(Integer setId);

}
