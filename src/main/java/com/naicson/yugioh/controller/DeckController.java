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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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

	@GetMapping("/todos")
	public List<Deck> consultar() {
		return deckRepository.findAll();
	}


	@GetMapping("/get-sets")
	@Operation(summary="Return summary Set informations with Pagination", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<Page<DeckSummaryDTO>> deckPagination(
		@PageableDefault(page = 0, size = 8, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
		@RequestParam String setType) {
			
		Page<DeckSummaryDTO> setList =  setsBySetType.findAllSetsByType(pageable, setType);
	
	    return new ResponseEntity<>(setList, HttpStatus.OK);

	}
	
	@GetMapping("/set-details")
	@Operation(summary="Return details of a Set", security = { @SecurityRequirement(name = "bearer-key") })
	@Cacheable(value = "setDetails")
	public ResponseEntity<SetDetailsDTO> setDetails(@RequestParam Long id, @RequestParam String source, @RequestParam String setType) {
		
		SetDetailsStrategy setDetailStrategy = getDetailByType
				.getOrDefault(SetDetailsType.valueOf(setType.toUpperCase()), null);

		return new ResponseEntity<>(setDetailStrategy.getSetDetails(id, source, false), HttpStatus.OK) ;
	}
	
	@GetMapping("/set-stats")
	@Operation(summary="Return details and stats of a Set", security = { @SecurityRequirement(name = "bearer-key") })
	//@Cacheable(value = "setStats")
	public ResponseEntity<SetDetailsDTO> getSetStats(@RequestParam Long id, @RequestParam String source, @RequestParam String setType){
		SetDetailsStrategy setDetailStrategy = getDetailByType
				.getOrDefault(SetDetailsType.valueOf(setType.toUpperCase()), null);

		return new ResponseEntity<>(setDetailStrategy.getSetDetails(id, source, true), HttpStatus.OK) ;
	}

	
	@GetMapping("/search-by-set-name")
	@Operation(summary="Search a Set by its Name and Source", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<Page<DeckSummaryDTO>> searchBySetName(@RequestParam("setName") String setName, 
			@RequestParam("source") SourceTypes source) {
		Page<DeckSummaryDTO> setsFound = this.deckService.searchBySetName(setName, source);
		
		return new ResponseEntity<>(setsFound, HttpStatus.OK);
	}

	@GetMapping("/autocomplete-sets")
	@Operation(summary="Return All Sets Name for Autocomplete ", security = { @SecurityRequirement(name = "bearer-key") })
	@Cacheable(value = "autocompleteSets")
	public ResponseEntity<List<AutocompleteSetDTO>> autocompleteSets(@RequestParam("source") String source) {
		
		List<AutocompleteSetDTO> autocompleteDto = deckService.autocompleteSet();
			
		return new ResponseEntity<>(autocompleteDto, HttpStatus.OK) ;
	}
	
	@GetMapping("/get-all-decksname")
	@Operation(summary="Get all Decks Name", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<DeckAndSetsBySetTypeDTO>> getAllDecksName(@RequestParam("collectionDeck") boolean collectionDeck){
		List<DeckAndSetsBySetTypeDTO> listDto = deckService.getAllDecksName(collectionDeck);
		
		return new ResponseEntity<>(listDto, HttpStatus.OK);
	}
	
	@PostMapping("/update-cards-quantity")
	@Operation(summary="Update cards quantity in a Deck", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> updateCardsQuantity(@RequestBody String setCodes){
		deckService.updateCardsQuantity(setCodes);

		return new ResponseEntity<>(JSONObject.quote("Update received!"), HttpStatus.OK);
	}
	
	@GetMapping("/get-deck-to-edit")
	@Operation(summary="Edit especific deck", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<SetEditDTO> getDeckToEdit(@RequestParam Integer deckId) {	
		return new ResponseEntity<>(deckService.getDeckToEdit(deckId), HttpStatus.OK);
	}
	
	@PostMapping("/edit-deck")
	@Operation(summary="Deck to edit", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> editDeck(@RequestBody SetEditDTO dto){
		deckService.editDeck(dto);
		
		return new ResponseEntity<>(JSONObject.quote("Deck edited successfully!"), HttpStatus.OK);
	}
	
//	@GetMapping("/update-deck-price")
//	@Operation(summary="Update price's Cards of a Deck", security = { @SecurityRequirement(name = "bearer-key") })
//	public ResponseEntity<String> updateDeckPrice(@RequestParam String deckName){
//		deckService.updateDeckPrice(deckName);
//		
//		return new ResponseEntity<>(JSONObject.quote("Deck price updated successfully!"), HttpStatus.OK);
//	}
//	

}
