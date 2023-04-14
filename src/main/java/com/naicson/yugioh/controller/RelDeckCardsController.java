package com.naicson.yugioh.controller;

import java.util.List;

import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({"yugiohAPI/relDeckCards"})
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class RelDeckCardsController {
	
	@Autowired
	RelDeckCardsServiceImpl service;
	
	@PostMapping("/edit-relation")
	@ApiOperation(value="Edit Relation", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> editRelDeckCards(@Valid @RequestBody RelDeckCards rel){
		service.editRelDeckCards(rel);
		return new ResponseEntity<>(JSONObject.quote("Relation edited successfully!"), HttpStatus.OK);
	}
	
	@DeleteMapping("/remove-relation")
	@ApiOperation(value="Remove Relation", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> removeRelDeckCards(@RequestParam Long relId){
		service.removeRelDeckCards(relId);
		
		return new ResponseEntity<>(JSONObject.quote("Relation REMOVED successfully!"), HttpStatus.OK);
	}
	
	@PostMapping("/create-relation")
	@ApiOperation(value="Create Relation", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> createRelation(@Valid @RequestBody RelDeckCards rel){
		service.createRelation(rel);
		return new ResponseEntity<>(JSONObject.quote("Relation created!"), HttpStatus.OK);
	}
	
	//@Cacheable("relation-by-deck-id")
	@GetMapping("/get-by-deck-id")
	@ApiOperation(value="Get Relation by Deck ID", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<RelDeckCards>> getRelationByDeckId(@RequestParam Integer deckId){
		return new ResponseEntity<>(service.getRelationByDeckId(deckId), HttpStatus.OK);
	}
}
