package com.naicson.yugioh.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;

import com.naicson.yugioh.dto.RelUserDeckDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.sets.DeckUsers;
import com.naicson.yugioh.repository.DeckRepository;
import com.naicson.yugioh.repository.sets.DeckUsersRepository;
import com.naicson.yugioh.service.DeckServiceImpl;
import com.naicson.yugioh.service.UserDetailsImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@RestController
@RequestMapping({ "yugiohAPI/decks" })
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class DeckController {

	@Autowired
	DeckRepository deckRepository;
	@Autowired
	DeckServiceImpl deckService;
	@Autowired
	DeckUsersRepository deckUserRepository;

	Page<Deck> deckList = null;
	Page<DeckUsers> deckUserList = null;

	@GetMapping("/todos")
	public List<Deck> consultar() {
		return deckRepository.findAll();
	}

	@GetMapping("/pagination")
	public ResponseEntity<Page<Deck>> deckPagination(
			@PageableDefault(page = 0, size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
			@RequestParam String setType) {

		if (!setType.equals("") && setType != null && !setType.equals("UD"))
			deckList = deckRepository.findAll(pageable);
		
		if (deckList.isEmpty()) {
			return new ResponseEntity<Page<Deck>>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(deckList, HttpStatus.OK);

	}

	@GetMapping("/sets-of-user")
	public ResponseEntity<Page<DeckUsers>> setsOfUser(
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

			return new ResponseEntity<Page<DeckUsers>>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(deckUserList, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Deck> deckAndCards(@RequestParam Long id, @RequestParam String source) throws Exception {
		Deck deck;	
		
		deck = deckService.deckAndCards(id, source);	
		
		return new ResponseEntity<>(deck, HttpStatus.OK) ;
	}
	
	@GetMapping("/edit-deck")
	public ResponseEntity<Deck> editDeck(@RequestParam("id") Long deckId, @RequestParam("setSource") String setSource){
		Deck deck = deckService.editDeck(deckId, setSource);
		
		return new ResponseEntity<Deck>(deck, HttpStatus.OK);
	}
	
	@GetMapping("/search-by-set-name")
	public ResponseEntity<List<Deck>> searchByDeckName(@RequestParam("setName") String setName, @RequestParam("source") String source) {
		List<Deck> setsFound = this.deckService.searchByDeckName(setName, source);
		
		return new ResponseEntity<List<Deck>>(setsFound, HttpStatus.OK);
	}

	@GetMapping(path = { "/add-deck-to-user-collection/{deckId}" })
	public int addSetToUserCollection(@PathVariable("deckId") Long deckId) throws Exception, ErrorMessage {
		if (deckId != null && deckId > 0) {
			return deckService.addSetToUserCollection(deckId);
		} else {
			throw new ErrorMessage("The deck informed is not valid!");
		}
	}

	@GetMapping(path = { "/remove-set-to-user-collection/{deckId}" })
	public int removeSetFromUsersCollection(@PathVariable("deckId") Long deckId) throws Exception, ErrorMessage {
		if (deckId != null && deckId > 0) {
			return deckService.removeSetFromUsersCollection(deckId);
		} else {
			throw new ErrorMessage("The deck informed is not valid!");
		}
	}

	@GetMapping("/rel-user-decks")
	public List<RelUserDeckDTO> searchForDecksUserHave(@RequestParam Long[] decksIds) throws SQLException, ErrorMessage {
		List<RelUserDeckDTO> rel = null;

		if (decksIds != null && decksIds.length > 0) {
			rel = deckService.searchForDecksUserHave(decksIds);
		}

		return rel;
	}
	
	@PostMapping(path = "/save-userdeck",  produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<String> saveUserDeck(@RequestBody Deck deck) throws SQLException{
		this.deckService.saveUserdeck(deck);
		List<String> retorno = List.of("Deck saved successfully");
		
		return retorno; 
	}

}