package com.naicson.yugioh.service.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.RelUserCardsDTO;
import com.naicson.yugioh.data.dto.cards.CardDetailsDTO;
import com.naicson.yugioh.data.dto.cards.CardOfArchetypeDTO;
import com.naicson.yugioh.data.dto.cards.CardOfUserDetailDTO;
import com.naicson.yugioh.data.dto.cards.CardsSearchDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.util.search.CardSpecification;
import com.naicson.yugioh.util.search.SearchCriteria;

@Service
public interface CardDetailService {
	
	Card cardDetails(Integer id);

	
	List<CardOfArchetypeDTO> findCardByArchetype(Integer archId);
	
	List<RelUserCardsDTO> searchForCardsUserHave(int[] cardsNumbers);
	
	CardOfUserDetailDTO cardOfUserDetails(Integer Integer);
	
	CardDetailsDTO findCardByNumberWithDecks(Long cardNumero);
	
	List<CardsSearchDTO> getByGenericType(Pageable page, String getByGenericType, long userId);
	
	Page<Card> findAll(CardSpecification spec,  Pageable pageable);

	List<CardsSearchDTO> cardSearch(List<SearchCriteria> criterias, String join, Pageable pageable);

	List<CardsSearchDTO> cardSearchByNameUserCollection(String cardName, Pageable pageable);

	List<Card> randomCardsDetailed();

	Page<Card> searchCardDetailed(List<SearchCriteria> criterias, String join, Pageable pageable);

	List<RelDeckCards> findAllRelDeckCardsByCardNumber(Integer cardNumber);
	
	List<Long> findCardsNotRegistered(List<Long> cardsNumber);

	Card listarNumero(Long numero);
	
	 Map<String, List<String>> findQtdCardUserHaveByCollection(Integer cardId, String nameAndQuantity);

	void updateCardsImages(String cardImages);
	
	List<CardsSearchDTO> getRandomCards();

}
