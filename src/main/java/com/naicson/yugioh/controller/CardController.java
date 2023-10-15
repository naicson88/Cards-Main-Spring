package com.naicson.yugioh.controller;

import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.cards.CardDetailsDTO;
import com.naicson.yugioh.data.dto.cards.CardOfUserDetailDTO;
import com.naicson.yugioh.data.dto.cards.CardsSearchDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.service.card.CardServiceImpl;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.search.SearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping({"yugiohAPI/cards"})
@CrossOrigin(origins = "${angular.path}", maxAge = 3600)
public class CardController {
	
	@Autowired
	CardServiceImpl cardService;
	@Autowired
	DeckServiceImpl deckService;
	
	Logger logger = LoggerFactory.getLogger(CardController.class);
	
	@Operation(summary="Return Card details by its Number", security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(path = {"number/{cardNumero}"})
	public ResponseEntity<CardDetailsDTO> procuraPorCardNumero(@PathVariable("cardNumero") Long cardNumero) {

		CardDetailsDTO card = cardService.findCardByNumberWithDecks(cardNumero);

		return new ResponseEntity<>(card, HttpStatus.OK);		
	}	
	
	@PostMapping(path = {"/searchCard"})
	@Operation(summary="Search cards and return a Pagination", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<CardsSearchDTO>> cardSearch(@PageableDefault(page = 0, size = 30, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable, 
			@RequestBody List<SearchCriteria> criterias, String join){
			
		List<CardsSearchDTO> list = cardService.cardSearch(criterias, join, pageable);
		
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@PostMapping(path = {"/searchCardDetailed"})
	@Operation(summary="Search cards and return detailed information with Pagination", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<Page<Card>> cardSearchDetailed(@PageableDefault(page = 1, size = 30, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable, 
			@RequestBody List<SearchCriteria> criterias, String join){
		Page<Card> listCards = cardService.searchCardDetailed(criterias, join, pageable);
		
		return new ResponseEntity<>(listCards, HttpStatus.OK);
	}
	
	@GetMapping(path = {"/randomCards"})
	@Operation(summary="Bring random Cards informations", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<CardsSearchDTO>> getRandomCards(){		
		List<CardsSearchDTO> dtoList = cardService.getRandomCards();
			
		return new ResponseEntity<>(dtoList, HttpStatus.OK);
	}
	
	@GetMapping(path = {"/randomCardsDetailed"})
	@Operation(summary="Bring detailed random Cards informations", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<Card>> randomCardsDetailed(){
		
		List<Card> cards = cardService.randomCardsDetailed();
		
		return new ResponseEntity<>(cards, HttpStatus.OK);
	}
	
	@GetMapping(path = {"/rel-user-cards"})
	@Operation(summary="Search for cards that user have", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<RelUserCardsDTO>> searchForCardsUserHave(@RequestParam int[] cardsNumbers) {

		List<RelUserCardsDTO> relList =	cardService.searchForCardsUserHave(cardsNumbers);
		
		return new ResponseEntity<>(relList, HttpStatus.OK);

	}
	
	@GetMapping(path = {"/load-cards-userscollection"})
	@Operation(summary="Search for cards from user collection", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<CardsSearchDTO>> loadCardsUserHave(@PageableDefault(page = 0, size = 30, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable,
			@RequestParam String genericType) {
			
			List<CardsSearchDTO> list = cardService.getByGenericType(pageable, genericType, GeneralFunctions.userLogged().getId());
			
			return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@GetMapping(path = {"/card-user-details"})
	@Operation(summary="Get details of a card that user have", security = { @SecurityRequirement(name = "bearer-key") })	
	public ResponseEntity<CardOfUserDetailDTO> cardOfUserDetails(@RequestParam Integer cardId) {

		CardOfUserDetailDTO cardDetailDTO = cardService.cardOfUserDetails(cardId);	
		
		return new ResponseEntity<>(cardDetailDTO, HttpStatus.OK);	
	}
	
	@GetMapping(path = {"/cardname-usercollection"})
	@Operation(summary="Search card by name of a user collection", security = { @SecurityRequirement(name = "bearer-key") })	
	public ResponseEntity<List<CardsSearchDTO>> cardSearchByNameUserCollection(
			@PageableDefault(page = 0, size = 30, sort = "nome", direction = Sort.Direction.ASC)
			Pageable pageable, @RequestParam String cardName) {
		
		List<CardsSearchDTO> cardList = cardService.cardSearchByNameUserCollection(cardName, pageable);
		
		return new ResponseEntity<>(cardList, HttpStatus.OK);
		
	}
	
	@GetMapping(path = {"/search-cardSetcodes"})
	@Operation(summary="Find all decks of a card by its Number", security = { @SecurityRequirement(name = "bearer-key") })	
	public ResponseEntity<List<RelDeckCards>> findAllRelDeckCardsByCardNumber(@RequestParam Integer cardId){
		List<RelDeckCards> relation = this.cardService.findAllRelDeckCardsByCardNumber(cardId);
		
		return new ResponseEntity<>(relation, HttpStatus.OK);
	}
	
	@PostMapping(path = {"/search-cards-not-registered"})
	@Operation(summary="Search Cards by its Numbers", security = { @SecurityRequirement(name = "bearer-key") })	
	public ResponseEntity<List<Long>> searchCardsByCardNumbers(@RequestBody List<Long> cardNumbers){
		
		logger.info("Start request for cards not registered...");
		
		List<Long> cardsNotRegistered = this.cardService.findCardsNotRegistered(cardNumbers);
		
		return new ResponseEntity<>(cardsNotRegistered, HttpStatus.OK);
	}
	
	@PostMapping(path = {"/update-images"})
	@Operation(summary="Update Card images passing the numbers", security = { @SecurityRequirement(name = "bearer-key") })	
	public ResponseEntity<String> updateCardsImages(@RequestBody String cardImagesJson){
		
		cardService.updateCardsImages(cardImagesJson);
		
		return new ResponseEntity<>(JSONObject.quote("Communication Success"), HttpStatus.OK);
	}
	
	@Cacheable("alternatives")
	@GetMapping(path = {"/get-alternative-numbers"})
	@Operation(summary="Get Card's alternative arts", security = { @SecurityRequirement(name = "bearer-key") })	
	public ResponseEntity<List<Long>> getAlternativeArts(@RequestParam Integer cardId){
	
		return new ResponseEntity<>(cardService.getAlternativeArts(cardId), HttpStatus.OK);
	}
	
	@Cacheable("card_names")
	@GetMapping(path = {"/get-all-card-names"})
	@Operation(summary="Get All Card's names and Ids", security = { @SecurityRequirement(name = "bearer-key") })	
	public ResponseEntity<Map<Integer, String>> getAllCardsNamesAndId(){
		return new ResponseEntity<>(cardService.getAllCardsNamesAndId(), HttpStatus.OK);
	}
}
