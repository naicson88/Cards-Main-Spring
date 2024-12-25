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
import com.naicson.yugioh.entity.sets.UserDeck;
import com.naicson.yugioh.repository.sets.UserDeckRepository;
import com.naicson.yugioh.service.deck.UserDeckServiceImpl;
import com.naicson.yugioh.service.setcollection.ISetsByType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping({ "yugiohAPI/userDeck" })
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserDeckController<T> {
	
	@Autowired
	UserDeckRepository deckUserRepository;
	
	@Autowired
	UserDeckServiceImpl userDeckService;
	
	@Autowired
	ISetsByType<T> setsBySetType;
	
	Page<DeckSummaryDTO> setList = null;
	

	@GetMapping("/sets-of-user")
	@Operation(summary="Return Sets of a User", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<Page<DeckSummaryDTO>> setsOfUser(
			@PageableDefault(page = 0, size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
			@RequestParam String setType) {
			
			setList = setsBySetType.findAllUserSetsByType(pageable, setType);

		return new ResponseEntity<>(setList, HttpStatus.OK);
	}
	
	@GetMapping("/edit-deck")
	@Operation(summary="Edit a Set by its ID and Source type", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<Deck> editUserDeck(@RequestParam("id") Long deckId, @RequestParam("setSource") String setSource){
		Deck deck = userDeckService.editUserDeck(deckId);
		
		return new ResponseEntity<>(deck, HttpStatus.OK);
	}
	
	@PostMapping(path = "/save-userdeck")
	@Operation(summary="Save a User Set", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> saveUserDeck(@RequestBody UserDeck deck) {
		
		this.userDeckService.saveUserDeck(deck);
		
		return new ResponseEntity<String>( JSONObject.quote("Deck saved successfully!"), HttpStatus.OK);

	}

	@GetMapping(path = { "/remove-set-to-user-collection/{deckId}" })
	@Operation(summary="Remove a Set from User collection", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> removeSetFromUsersCollection(@PathVariable("deckId") Long deckId) {
		
		 userDeckService.removeSetFromUsersCollection(deckId);
		
		return new ResponseEntity<String>(JSONObject.quote("Set was successfully removed from your collection"), HttpStatus.OK);

	}
	
	@GetMapping(path = { "/add-deck-to-user-collection/{deckId}" })
	@Operation(summary="Add a Set to User collection", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<Integer> addSetToUserCollection(@PathVariable("deckId") Long deckId) {
		
		 userDeckService.addSetToUserCollection(deckId);
		
		return new ResponseEntity<Integer>(1, HttpStatus.OK);
					
	}
	
	@GetMapping("/get-all-decksname")
	@Operation(summary="Get all Decks Name of user", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<DeckAndSetsBySetTypeDTO>> getAllDecksName(){
		List<DeckAndSetsBySetTypeDTO> listDto = userDeckService.getAllDecksName();
		
		return new ResponseEntity<List<DeckAndSetsBySetTypeDTO>>(listDto, HttpStatus.OK);
	}
	
	@GetMapping("/deck-transfer")
	@Operation(summary="Get a Deck and Cards for transfer", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<UserSetCollectionDTO> getDeckAndCardsForTransfer(@RequestParam("deckId") Integer deckId){
		
		UserSetCollectionDTO dto = userDeckService.getDeckAndCardsForTransfer(deckId.longValue());
		
		return new ResponseEntity<UserSetCollectionDTO>(dto, HttpStatus.OK);
	}
	
	@PostMapping("/create-based-deck")
	@Operation(summary="Create a Based Deck", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<Long> createBasedDeck(@RequestBody Integer konamiDeckId){
		Long createdDeckId = userDeckService.createBasedDeck(konamiDeckId.longValue());
		
		return new ResponseEntity<Long>(createdDeckId, HttpStatus.CREATED);
	}
	
}
