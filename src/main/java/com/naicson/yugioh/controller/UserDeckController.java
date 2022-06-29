package com.naicson.yugioh.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import com.naicson.yugioh.data.dto.RelUserDeckDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.GeneralFunctions;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({ "yugiohAPI/userDeck" })
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserDeckController {
	
	@Autowired
	UserDeckRepository deckUserRepository;
	
	@Autowired
	UserDeckServiceImpl deckService;
	
	Page<UserDeck> deckUserList = null;
	

	@GetMapping("/sets-of-user")
	@ApiOperation(value="Return Sets of a User", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<Page<UserDeck>> setsOfUser(
			@PageableDefault(page = 0, size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
			@RequestParam String setType) {
		
			switch(setType) {
				case "UD": setType =  "D"; break;
				case "UB": setType =  "B"; break;
				case "UT": setType =  "T"; break;				
			}

			UserDetailsImpl user = GeneralFunctions.userLogged();
			
			deckUserList = deckUserRepository.findAllByUserIdAndSetType(user.getId(), setType, pageable);

		if (deckUserList == null) {

			return new ResponseEntity<Page<UserDeck>>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(deckUserList, HttpStatus.OK);
	}
	
	@GetMapping("/edit-deck")
	@ApiOperation(value="Edit a Set by its ID and Source type", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<Deck> editUserDeck(@RequestParam("id") Long deckId, @RequestParam("setSource") String setSource){
		Deck deck = deckService.editUserDeck(deckId);
		
		return new ResponseEntity<Deck>(deck, HttpStatus.OK);
	}
	
	@PostMapping(path = "/save-userdeck")
	@ApiOperation(value="Save a User Set", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> saveUserDeck(@RequestBody Deck deck) {
		
		this.deckService.saveUserdeck(deck);
		
		return new ResponseEntity<String>( JSONObject.quote("Deck saved successfully!"), HttpStatus.OK);

	}

	@GetMapping(path = { "/remove-set-to-user-collection/{deckId}" })
	@ApiOperation(value="Remove a Set from User collection", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> removeSetFromUsersCollection(@PathVariable("deckId") Long deckId) {
		
		 deckService.removeSetFromUsersCollection(deckId);
		
		return new ResponseEntity<String>("Set was successfully removed from your collection", HttpStatus.OK);

	}
	
	@GetMapping(path = { "/add-deck-to-user-collection/{deckId}" })
	@ApiOperation(value="Add a Set to User collection", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<Integer> addSetToUserCollection(@PathVariable("deckId") Long deckId) {
		
		Integer qtdAdded = deckService.addSetToUserCollection(deckId);
		
		return new ResponseEntity<Integer>(qtdAdded, HttpStatus.OK);
					
	}
	

	@GetMapping("/rel-user-decks")
	@ApiOperation(value="Search for a Set that User have", authorizations = { @Authorization(value="JWT") })	
	public List<RelUserDeckDTO> searchForDecksUserHave(@RequestParam Long[] decksIds) {

		List<RelUserDeckDTO> rel = deckService.searchForDecksUserHave(decksIds);
		
		return rel;
	}

	
}
