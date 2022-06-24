package com.naicson.yugioh.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.data.dto.RelUserDeckDTO;
import com.naicson.yugioh.data.dto.set.DeckSummaryDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.repository.DeckRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.interfaces.SetCollectionService;
import com.naicson.yugioh.service.setcollection.ISetsByType;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({ "yugiohAPI/decks" })
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class DeckController<T> {

	@Autowired
	DeckRepository deckRepository;
	
	@Autowired
	DeckServiceImpl deckService;
	
	@Autowired
	SetCollectionService setCollService;
	
	@Autowired
	ISetsByType<T> setsBySetType;
	
	Page<DeckSummaryDTO> setList = null;
	

	@GetMapping("/todos")
	public List<Deck> consultar() {
		return deckRepository.findAll();
	}

	@GetMapping("/get-sets")
	@ApiOperation(value="Return summary Set informations with Pagination", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<Page<DeckSummaryDTO>> deckPagination(
		@PageableDefault(page = 0, size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
		@RequestParam String setType) {
			
	    setList =  setsBySetType.findAllSetsByType(pageable, setType);
	
	    return new ResponseEntity<>(setList, HttpStatus.OK);

	}

	@GetMapping("/set-details")
	@ApiOperation(value="Return details of a Set", authorizations = { @Authorization(value="JWT") })
	@Cacheable(value = "setDetails")
	public ResponseEntity<SetDetailsDTO> setDetails(@RequestParam Long id, @RequestParam String source, @RequestParam String setType) {
		SetDetailsDTO deck = null;	
		
		if("DECK".equals(setType))
			deck = deckService.deckAndCards(id, source);		
		else 
			deck = setCollService.setCollectionDetailsAsDeck(id, source);
			
		return new ResponseEntity<>(deck, HttpStatus.OK) ;
	}	
	
	@GetMapping("/search-by-set-name")
	@ApiOperation(value="Search a Set by its Name and Source", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<Deck>> searchByDeckName(@RequestParam("setName") String setName, @RequestParam("source") String source) {
		List<Deck> setsFound = this.deckService.searchByDeckName(setName, source);
		
		return new ResponseEntity<List<Deck>>(setsFound, HttpStatus.OK);
	}


	

}
