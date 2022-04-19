package com.naicson.yugioh.service.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.cards.CardAndSetsDTO;
import com.naicson.yugioh.data.dto.cards.CardDetailsDTO;
import com.naicson.yugioh.data.dto.cards.CardOfArchetypeDTO;
import com.naicson.yugioh.data.dto.cards.CardOfUserDetailDTO;
import com.naicson.yugioh.data.dto.cards.CardsSearchDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.Deck;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.util.exceptions.ErrorMessage;
import com.naicson.yugioh.util.search.CardSpecification;
import com.naicson.yugioh.util.search.SearchCriteria;

@Service
public interface CardDetailService {
	
	Card cardDetails(Integer id);
	
	List<Deck> cardDecks(Long cardNumero);	
	
	List<CardOfArchetypeDTO> findCardByArchetype(Integer archId);
	
	List<RelUserCardsDTO> searchForCardsUserHave(int[] cardsNumbers);
	
	CardOfUserDetailDTO cardOfUserDetails(Long cardNumber);
	
	CardDetailsDTO findCardByNumberWithDecks(Long cardNumero);
	
	List<CardsSearchDTO> getByGenericType(Pageable page, String getByGenericType, long userId);
	
	Page<Card> findAll(CardSpecification spec,  Pageable pageable);

	List<CardsSearchDTO> cardSearch(List<SearchCriteria> criterias, String join, Pageable pageable);

	List<CardsSearchDTO> cardSearchByNameUserCollection(String cardName, Pageable pageable);

	List<Card> randomCardsDetailed();

	Page<Card> searchCardDetailed(List<SearchCriteria> criterias, String join, Pageable pageable);

	List<RelDeckCards> findAllRelDeckCardsByCardNumber(Long cardNumber);
	
	List<Long> findCardsNotRegistered(List<Long> cardsNumber);

	Card listarNumero(Long numero);
	
	 Map<String, Integer> findQtdCardUserHaveByCollection(Integer cardId, String collectionSource);

}
