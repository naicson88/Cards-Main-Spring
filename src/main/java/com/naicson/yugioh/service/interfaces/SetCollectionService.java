package com.naicson.yugioh.service.interfaces;

import cardscommons.dto.AssociationDTO;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.SetEditDTO;
import com.naicson.yugioh.entity.sets.SetCollection;

import javax.validation.Valid;
import java.util.List;

public interface SetCollectionService {
	
	public SetCollection findById(Integer id);
	
	public SetCollection saveSetCollection(SetCollection setCollection);
	
//	public SetDetailsDTO setCollectionDetailsAsDeck(Long setId, String source);

	public List<DeckAndSetsBySetTypeDTO> getAllSetsBySetType(String setType);

	public AssociationDTO newAssociation(@Valid AssociationDTO dto);

	public SetEditDTO getCollectionToEdit(Integer setId);

	public SetCollection editCollection(SetEditDTO dto);

}
