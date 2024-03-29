package com.naicson.yugioh.controller;

import java.util.List;

import javax.validation.Valid;

import cardscommons.dto.AssociationDTO;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.SetEditDTO;
import com.naicson.yugioh.data.strategy.setDetails.CollectionDetailsStrategy;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.service.interfaces.SetCollectionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping({ "yugiohAPI/collection" })
@CrossOrigin(origins = "*", maxAge = 3600)
public class SetCollectionController {
	
	@Autowired
	SetCollectionService service;
	
	@Autowired
	CollectionDetailsStrategy collectionStrategy;
	
	@GetMapping("/{id}")
	@Operation(summary="Get a Set Collection by its ID", security = { @SecurityRequirement(name = "bearer-key") })
	public SetCollection findById(@PathVariable("id") Integer id) {
		return service.findById(id);
	}
	
	@GetMapping("/setsname-by-settype/{setType}")
	@Operation(summary="Get all Decks and Sets by SetType", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<DeckAndSetsBySetTypeDTO>> getAllSetsBySetType(@PathVariable("setType") String setType){
		List<DeckAndSetsBySetTypeDTO> dto = service.getAllSetsBySetType(setType);
		
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
	
	@PostMapping("/new-association")
	public ResponseEntity<String> newAssociation(@Valid @RequestBody AssociationDTO dto){
		service.newAssociation(dto);
		
		return new ResponseEntity<>(JSONObject.quote("Association confirmed!"), HttpStatus.OK);
	}
	
	@GetMapping("/collection-to-edit")
	@Operation(summary="Get especific Collection DTO to edit", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<SetEditDTO> getCollectionToEdit(@RequestParam Integer deckId){	
		
		return new ResponseEntity<>( service.getCollectionToEdit(deckId), HttpStatus.OK);
	}
	
	@PostMapping("/edit-collection")
	@Operation(summary="Edit especific Collection", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> editCollection(@RequestBody SetEditDTO dto){
		service.editCollection(dto);
		return new ResponseEntity<>( JSONObject.quote("SetCollection edited successfully!"), HttpStatus.OK);
	}
	
}
