package com.naicson.yugioh.controller;

import java.util.List;

import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping({"yugiohAPI/relDeckCards"})
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class RelDeckCardsController {
	
	@Autowired
	RelDeckCardsServiceImpl service;
	
	@PostMapping("/edit-relation")
	@Operation(summary="Edit Relation", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> editRelDeckCards(@Valid @RequestBody RelDeckCards rel){
		service.editRelDeckCards(rel);
		return new ResponseEntity<>(JSONObject.quote("Relation edited successfully!"), HttpStatus.OK);
	}
	
	@DeleteMapping("/remove-relation")
	@Operation(summary="Remove Relation", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> removeRelDeckCards(@RequestParam Long relId){
		service.removeRelDeckCards(relId);
		
		return new ResponseEntity<>(JSONObject.quote("Relation REMOVED successfully!"), HttpStatus.OK);
	}
	
	@PostMapping("/create-relation")
	@Operation(summary="Create Relation", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> createRelation(@Valid @RequestBody RelDeckCards rel){
		service.createRelation(rel);
		return new ResponseEntity<>(JSONObject.quote("Relation created!"), HttpStatus.OK);
	}
	
	//@Cacheable("relation-by-deck-id")
	@GetMapping("/get-by-deck-id")
	@Operation(summary="Get Relation by Deck ID", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<RelDeckCards>> getRelationByDeckId(@RequestParam Integer deckId){
		return new ResponseEntity<>(service.getRelationByDeckId(deckId), HttpStatus.OK);
	}
}
