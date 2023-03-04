package com.naicson.yugioh.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.data.bridge.source.SourceTypes;
import com.naicson.yugioh.data.dto.set.AutocompleteSetDTO;
import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.DeckSummaryDTO;
import com.naicson.yugioh.data.dto.set.SetDetailsDTO;
import com.naicson.yugioh.data.dto.set.SetEditDTO;
import com.naicson.yugioh.data.strategy.setDetails.SetDetailsStrategy;
import com.naicson.yugioh.data.strategy.setDetails.SetDetailsType;
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
	
	private final Map<SetDetailsType, SetDetailsStrategy> getDetailByType;
	
	public DeckController(Map<SetDetailsType, SetDetailsStrategy> getDetailByType) {
		this.getDetailByType = getDetailByType;
	}

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
		
		SetDetailsStrategy setDetailStrategy = getDetailByType
				.getOrDefault(SetDetailsType.valueOf(setType.toUpperCase()), null);

		return new ResponseEntity<>(setDetailStrategy.getSetDetails(id, source), HttpStatus.OK) ;
	}

	
	@GetMapping("/search-by-set-name")
	@ApiOperation(value="Search a Set by its Name and Source", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<Page<DeckSummaryDTO>> searchBySetName(@RequestParam("setName") String setName, 
			@RequestParam("source") SourceTypes source) {
		Page<DeckSummaryDTO> setsFound = this.deckService.searchBySetName(setName, source);
		
		return new ResponseEntity<>(setsFound, HttpStatus.OK);
	}

	@GetMapping("/autocomplete-sets")
	@ApiOperation(value="Return All Sets Name for Autocomplete ", authorizations = { @Authorization(value="JWT") })
	@Cacheable(value = "autocompleteSets")
	public ResponseEntity<List<AutocompleteSetDTO>> autocompleteSets(@RequestParam("source") String source) {
		
		List<AutocompleteSetDTO> autocompleteDto = deckService.autocompleteSet();
			
		return new ResponseEntity<>(autocompleteDto, HttpStatus.OK) ;
	}
	
	@GetMapping("/get-all-decksname")
	@ApiOperation(value="Get all Decks Name", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<DeckAndSetsBySetTypeDTO>> getAllDecksName(@RequestParam("collectionDeck") boolean collectionDeck){
		List<DeckAndSetsBySetTypeDTO> listDto = deckService.getAllDecksName(collectionDeck);
		
		return new ResponseEntity<>(listDto, HttpStatus.OK);
	}
	
	@PostMapping("/update-cards-quantity")
	@ApiOperation(value="Update cards quantity in a Deck", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> updateCardsQuantity(@RequestBody String setCodes){
		deckService.updateCardsQuantity(setCodes);

		return new ResponseEntity<>(JSONObject.quote("Update received!"), HttpStatus.OK);
	}
	
	@GetMapping("/get-deck-to-edit")
	@ApiOperation(value="Edit especific deck", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<SetEditDTO> getDeckToEdit(@RequestParam Integer deckId){	
		return new ResponseEntity<>(deckService.getDeckToEdit(deckId), HttpStatus.OK);
	}
	
	@PostMapping("/edit-deck")
	@ApiOperation(value="Deck to edit", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> editDeck(@RequestBody SetEditDTO dto){
		deckService.editDeck(dto);
		
		return new ResponseEntity<>(JSONObject.quote("Deck edited successfully!"), HttpStatus.OK);
	}
	

}
