package com.naicson.yugioh.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.cards.CardDetailsDTO;
import com.naicson.yugioh.data.dto.cards.CardOfUserDetailDTO;
import com.naicson.yugioh.data.dto.cards.CardsSearchDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.repository.CardRepository;
import com.naicson.yugioh.repository.RelDeckCardsRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.service.interfaces.CardDetailService;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.exceptions.ErrorMessage;
import com.naicson.yugioh.util.search.SearchCriteria;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;


@RestController
@RequestMapping({"yugiohAPI/cards"})
@CrossOrigin(origins = "${angular.path}", maxAge = 3600)
public class CardController {
	
	@Autowired
	CardDetailService cardService;
	@Autowired
	DeckServiceImpl deckService;
	@Autowired
	CardRepository cardRepository;
	@Autowired
	RelDeckCardsRepository relDeckCardsRepository;
	
	Logger logger = LoggerFactory.getLogger(DeckServiceImpl.class);
	
	@ApiOperation(value="Return Card by its Number", authorizations = { @Authorization(value="JWT") })
	@GetMapping(path = {"num/{numero}"})
	public Card listarNumero(@PathVariable("numero") Long numero) {
		return cardService.listarNumero(numero);
	}
	
	@ApiOperation(value="Return Card details by its Number", authorizations = { @Authorization(value="JWT") })
	@GetMapping(path = {"number/{cardNumero}"})
	public ResponseEntity<CardDetailsDTO> procuraPorCardNumero(@PathVariable("cardNumero") Long cardNumero) {

		CardDetailsDTO card = cardService.findCardByNumberWithDecks(cardNumero);

		return new ResponseEntity<CardDetailsDTO>(card, HttpStatus.OK);		
	}	
	
	@PostMapping(path = {"/searchCard"})
	@ApiOperation(value="Search cards and return a Pagination", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<CardsSearchDTO>> cardSearch(@PageableDefault(page = 0, size = 30, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable, 
			@RequestBody List<SearchCriteria> criterias, String join){
			
		List<CardsSearchDTO> list = cardService.cardSearch(criterias, join, pageable);
		
		return new ResponseEntity<List<CardsSearchDTO>>(list, HttpStatus.OK);
	}
	
	@PostMapping(path = {"/searchCardDetailed"})
	@ApiOperation(value="Search cards and return detailed information with Pagination", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<Page<Card>> cardSearchDetailed(@PageableDefault(page = 1, size = 30, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable, 
			@RequestBody List<SearchCriteria> criterias, String join){
		Page<Card> listCards = cardService.searchCardDetailed(criterias, join, pageable);
		
		return new ResponseEntity<Page<Card>>(listCards, HttpStatus.OK);
	}
	
	@GetMapping(path = {"/randomCards"})
	@ApiOperation(value="Bring random Cards informations", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<CardsSearchDTO>> randomCards(){
		List<CardsSearchDTO> dtoList = new ArrayList<>();
		
		List<Card> list = cardRepository.findRandomCards();
		
		for(Card card : list) {
			if(list != null && list.size() > 0) 
				dtoList.add(CardsSearchDTO.transformInDTO(card));
		}
		
		return new ResponseEntity<List<CardsSearchDTO>>(dtoList, HttpStatus.OK);
	}
	
	@GetMapping(path = {"/randomCardsDetailed"})
	@ApiOperation(value="Bring detailed random Cards informations", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<Card>> randomCardsDetailed(){
		
		List<Card> cards = cardService.randomCardsDetailed();
		
		return new ResponseEntity<List<Card>>(cards, HttpStatus.OK);
	}
	
	@GetMapping(path = {"/rel-user-cards"})
	@ApiOperation(value="Search for cards that user have", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<RelUserCardsDTO>> searchForCardsUserHave(@RequestParam int[] cardsNumbers) throws SQLException, ErrorMessage {
		List<RelUserCardsDTO> rel = null;
		
		if(cardsNumbers != null && cardsNumbers.length > 0) {
			rel = cardService.searchForCardsUserHave(cardsNumbers);
		}
		
		if(rel != null && rel.size() > 0) {
			return new ResponseEntity<List<RelUserCardsDTO>>(rel, HttpStatus.OK);
		} else {
			return new ResponseEntity<List<RelUserCardsDTO>>(rel, HttpStatus.NO_CONTENT);
		}
	}
	
	@GetMapping(path = {"/load-cards-userscollection"})
	@ApiOperation(value="Search for cards from user collection", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<CardsSearchDTO>> loadCardsUserHave(@PageableDefault(page = 0, size = 30, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable,
			@RequestParam String genericType) throws SQLException, ErrorMessage{
		
		try {
			if(genericType == null)
				throw new ErrorMessage(" No generic type was informed.");
			
			UserDetailsImpl user = GeneralFunctions.userLogged(); 
			
			List<CardsSearchDTO> list = cardService.getByGenericType(pageable, genericType, user.getId());
			
			return new ResponseEntity<List<CardsSearchDTO>>(list, HttpStatus.OK);
						
		}catch (ErrorMessage me) {
			throw me;
		}catch (Exception e) {
			throw e;
		}
	}
	
	@GetMapping(path = {"/card-user-details"})
	@ApiOperation(value="Get details of a card that user have", authorizations = { @Authorization(value="JWT") })	
	public ResponseEntity<CardOfUserDetailDTO> cardOfUserDetails(@RequestParam Integer cardId) {

		CardOfUserDetailDTO cardDetailDTO = cardService.cardOfUserDetails(cardId);	
		
		return new ResponseEntity<CardOfUserDetailDTO>(cardDetailDTO, HttpStatus.OK);	
	}
	
	@GetMapping(path = {"/cardname-usercollection"})
	@ApiOperation(value="Search card by name of a user collection", authorizations = { @Authorization(value="JWT") })	
	public ResponseEntity<List<CardsSearchDTO>> cardSearchByNameUserCollection(
			@PageableDefault(page = 0, size = 30, sort = "nome", direction = Sort.Direction.ASC)
			Pageable pageable, @RequestParam String cardName) {
		
		List<CardsSearchDTO> cardList = cardService.cardSearchByNameUserCollection(cardName, pageable);
		
		return new ResponseEntity<List<CardsSearchDTO>>(cardList, HttpStatus.OK);
		
	}
	
	@GetMapping(path = {"/search-cardSetcodes"})
	@ApiOperation(value="Find all decks of a card by its Number", authorizations = { @Authorization(value="JWT") })	
	public ResponseEntity<List<RelDeckCards>> findAllRelDeckCardsByCardNumber(@RequestParam Long cardNumber){
		List<RelDeckCards> relation = this.cardService.findAllRelDeckCardsByCardNumber(cardNumber);
		
		return new ResponseEntity<List<RelDeckCards>>(relation, HttpStatus.OK);
	}
	
	@PostMapping(path = {"/search-cards-not-registered"})
	@ApiOperation(value="Search Cards by its Numbers", authorizations = { @Authorization(value="JWT") })	
	public ResponseEntity<List<Long>> searchCardsByCardNumbers(@RequestBody List<Long> cardNumbers){
		
		logger.info("Start request for cards not registered...".toUpperCase());
		
		List<Long> cardsNotRegistered = this.cardService.findCardsNotRegistered(cardNumbers);
		
		return new ResponseEntity<List<Long>>(cardsNotRegistered, HttpStatus.OK);
	}
}

