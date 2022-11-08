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

import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.DeckSummaryDTO;
import com.naicson.yugioh.data.dto.set.UserSetCollectionDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.service.setcollection.ISetsByType;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({ "yugiohAPI/userDeck" })
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserDeckController<T> {
	
	@Autowired
	UserDeckServiceImpl userDeckService;
	
	@Autowired
	ISetsByType<T> setsBySetType;
	
	Page<DeckSummaryDTO> setList = null;
	

	@GetMapping("/sets-of-user")
	@ApiOperation(value="Return Sets of a User", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<Page<DeckSummaryDTO>> setsOfUser(
			@PageableDefault(page = 0, size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
			@RequestParam String setType) {
			
			setList = setsBySetType.findAllUserSetsByType(pageable, setType);

		return new ResponseEntity<>(setList, HttpStatus.OK);
	}
	
	@GetMapping("/edit-deck")
	@ApiOperation(value="Edit a Set by its ID and Source type", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<Deck> editUserDeck(@RequestParam("id") Long deckId, @RequestParam("setSource") String setSource){
		Deck deck = userDeckService.editUserDeck(deckId);
		
		return new ResponseEntity<>(deck, HttpStatus.OK);
	}
	
	@PostMapping(path = "/save-userdeck")
	@ApiOperation(value="Save a User Set", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> saveUserDeck(@RequestBody Deck deck) {
		
		this.userDeckService.saveUserdeck(deck, null);
		
		return new ResponseEntity<>( JSONObject.quote("Deck saved successfully!"), HttpStatus.OK);

	}

	@GetMapping(path = { "/remove-set-to-user-collection/{deckId}" })
	@ApiOperation(value="Remove a Set from User collection", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> removeSetFromUsersCollection(@PathVariable("deckId") Long deckId) {
		
		 userDeckService.removeSetFromUsersCollection(deckId);
		
		return new ResponseEntity<>(JSONObject.quote("Set was successfully removed from your collection"), HttpStatus.OK);

	}
	
	@GetMapping(path = { "/add-deck-to-user-collection/{deckId}" })
	@ApiOperation(value="Add a Set to User collection", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<Integer> addSetToUserCollection(@PathVariable("deckId") Long deckId) {
		
		 userDeckService.addSetToUserCollection(deckId);
		
		return new ResponseEntity<>(1, HttpStatus.OK);
					
	}
	
	@GetMapping("/get-all-decksname")
	@ApiOperation(value="Get all Decks Name of user", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<DeckAndSetsBySetTypeDTO>> getAllDecksName(){
		List<DeckAndSetsBySetTypeDTO> listDto = userDeckService.getAllDecksName();
		
		return new ResponseEntity<List<DeckAndSetsBySetTypeDTO>>(listDto, HttpStatus.OK);
	}
	
	@GetMapping("/deck-transfer")
	@ApiOperation(value="Get a Deck and Cards for transfer", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<UserSetCollectionDTO> getDeckAndCardsForTransfer(@RequestParam("deckId") Integer deckId){
		
		UserSetCollectionDTO dto = userDeckService.getDeckAndCardsForTransfer(deckId.longValue());
		
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
	
	@PostMapping("/create-based-deck")
	@ApiOperation(value="Create a Based Deck", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<Long> createBasedDeck(@RequestBody Integer konamiDeckId){
		Long createdDeckId = userDeckService.createBasedDeck(konamiDeckId.longValue());
		
		return new ResponseEntity<>(createdDeckId, HttpStatus.CREATED);
	}
	
}
