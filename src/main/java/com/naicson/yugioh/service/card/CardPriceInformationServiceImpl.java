package com.naicson.yugioh.service.card;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import cardscommons.dto.PriceDTO;
import com.naicson.yugioh.util.CardServicesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.dto.home.RankingForHomeDTO;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.RelDeckCards;
import com.naicson.yugioh.entity.stats.CardPriceInformation;
import com.naicson.yugioh.repository.CardPriceInformationRepository;
import com.naicson.yugioh.service.deck.RelDeckCardsServiceImpl;
import com.naicson.yugioh.util.enums.CardStats;
import com.naicson.yugioh.util.search.GeneralSpecification;
import com.naicson.yugioh.util.search.SearchCriteria;
import com.naicson.yugioh.util.search.SearchOperation;

@Service
public class CardPriceInformationServiceImpl {

	@Autowired
	private CardPriceInformationRepository cardInfoRepository;
	@Autowired
	private CardServicesUtil cardServicesUtil;

	@Autowired
	private RelDeckCardsServiceImpl relDeckService;

	Logger logger = LoggerFactory.getLogger(CardPriceInformationServiceImpl.class);

	public void saveWeeklyPercentageVariable(CardPriceInformation cardInfo) {
		this.validateWeeklyPercentageObj(cardInfo);
		cardInfo.setWeeklyPercentVariable(calculateWeeklyPercentageVariable(cardInfo));
		cardInfo.setLastUpdate(LocalDateTime.now());
		this.saveCardPriceInfo(cardInfo);
	}
	
	private double calculateWeeklyPercentageVariable(CardPriceInformation cardInfo) {
		Double diference = cardInfo.getCurrentPrice() - cardInfo.getPrice2();

		if (diference == 0.0) 
			return diference;
		
		Double percentDiff = (diference / cardInfo.getPrice2()) * 100;
		
		return BigDecimal.valueOf(percentDiff).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}
	
	@Transactional(rollbackFor = Exception.class)
	public CardPriceInformation saveCardPriceInfo(CardPriceInformation cardInfo) {
		return cardInfoRepository.save(cardInfo);
	}

	private void validateWeeklyPercentageObj(CardPriceInformation cardInfo) {
		if (cardInfo == null)
			throw new IllegalArgumentException("Card Information is null, cannot calculate weekly percentage");
		if (cardInfo.getCurrentPrice() == null || cardInfo.getCurrentPrice() == 0.0)
			throw new IllegalArgumentException("Card current price is invalid to calculate weekly percentage");
		if (cardInfo.getPrice2() == null || cardInfo.getPrice2() == 0.0)
			throw new IllegalArgumentException("Card price_2 is invalid to calculate weekly percentage");

	}
	public List<RankingForHomeDTO> getWeeklyHighStats() {
		return this.findWeeklyCards(CardStats.HIGH);
	}

	public List<RankingForHomeDTO> getWeeklyLowStats() {
		return this.findWeeklyCards(CardStats.LOW);
	}

	public List<RankingForHomeDTO> findWeeklyCards(CardStats stats) {

		if (stats == null)
			throw new IllegalArgumentException("Invalid stats for weekly consulting");

		List<CardPriceInformation> cardsByStats = stats.getStats(cardInfoRepository);

		this.validadeStatsList(cardsByStats, stats);

		List<RankingForHomeDTO> rankingByStatsList = this.convertFromCardInfoToRankingDTO(cardsByStats, stats);

		if (rankingByStatsList == null || rankingByStatsList.isEmpty())
			throw new NoSuchElementException("Ranking stats list is empty");

		return rankingByStatsList;
	}

	private List<RankingForHomeDTO> convertFromCardInfoToRankingDTO(List<CardPriceInformation> cardsByStats,
			CardStats stats) {

		this.validadeStatsList(cardsByStats, stats);

		return cardsByStats.stream().map(card -> {
			String cardName = this.getCardName(card.getCardNumber());
			RankingForHomeDTO cardByStats = new RankingForHomeDTO();
			cardByStats.setCardName(cardName);
			cardByStats.setCardNumber(String.valueOf(card.getCardNumber()));
			cardByStats.setCardPrice(card.getCurrentPrice());
			cardByStats.setPercentVariable(card.getWeeklyPercentVariable());
			cardByStats.setSetCode(card.getCardSetCode());
			return cardByStats;
		}).toList();
	}

	private String getCardName(Long cardNumber) {
		Card card = cardServicesUtil.cardServiceFindByNumero(cardNumber);

		if (card.getNome() == null || card.getNome().isBlank())
			throw new NoSuchElementException("Invalid card name of card = " + card.getNumero() + "".toUpperCase());

		return card.getNome();
	}

	private void validadeStatsList(List<CardPriceInformation> cardsByStats, CardStats stats) {
		if (cardsByStats == null || cardsByStats.isEmpty())
			throw new IllegalArgumentException("List of cards with stats " + stats.toString() + " is empty");
	}

	public List<CardPriceInformation> getAllPricesOfACardById(Integer cardId) {

		if (cardId == null || cardId == 0)
			throw new IllegalArgumentException("Invalid card id for search prices");

		return cardInfoRepository.findByCardId(cardId).orElse(Collections.emptyList());

	}

	@Transactional(rollbackFor = Exception.class)
	public void updateCardPrice(List<PriceDTO> prices) {
		List<PriceDTO> listCardsNotFound = new ArrayList<>();

		for (PriceDTO price : prices) {
			List<RelDeckCards> listRel = findRelDeckByCriterias(price);

			if (listRel == null || listRel.isEmpty()) {
				listCardsNotFound.add(price);
				
			} else {
				listRel.forEach(rel -> {
					List<CardPriceInformation> cardsInfo = cardInfoRepository.findByCardSetCode(rel.getCardSetCode());
					
					if (cardsInfo == null || cardsInfo.isEmpty())
						createNewCardInfoFromRelDeckCards(price, rel);
					else if (price.getPrice() > 0.0)
						updateHistoricalCardPrice(price, cardsInfo);
				});

				this.saveNewCardPrice(price, listRel);
			}

		}

		this.updateCardsNotFound(listCardsNotFound);

	}
	
	private void saveNewCardPrice(PriceDTO price, List<RelDeckCards> listRel) {
		RelDeckCards rel = listRel.get(0);
		rel.setCard_price(price.getPrice() > 0.0 ? price.getPrice() : rel.getCard_price());
		logger.info("Saving new Card Price {} on RelDeckCards", rel.getCard_price());
		relDeckService.save(rel);
	}

	private void updateHistoricalCardPrice(PriceDTO price, List<CardPriceInformation> cardsInfo) {
		CardPriceInformation cardInfo = cardsInfo.get(0);
		if(cardInfo.getLastUpdate().plusDays(3L).isBefore(LocalDateTime.now())) {
			cardInfo.setPrice5(cardInfo.getPrice4());
			cardInfo.setPrice4(cardInfo.getPrice3());
			cardInfo.setPrice3(cardInfo.getPrice2());
			cardInfo.setPrice2(cardInfo.getCurrentPrice());
			cardInfo.setCurrentPrice(price.getPrice());
			cardInfo.setLastUpdate(LocalDateTime.now());
			cardInfo.setWeeklyPercentVariable(this.calculateWeeklyPercentageVariable(cardInfo));
			logger.info("Updating Card Price {}", cardInfo.getCardSetCode());
			saveCardPriceInfo(cardInfo);
		}	
	}

	private void createNewCardInfoFromRelDeckCards(PriceDTO price, RelDeckCards rel) {
		CardPriceInformation cardInfo = new CardPriceInformation();
		cardInfo.setCardId(rel.getCardId());
		cardInfo.setCardNumber(rel.getCardNumber());
		cardInfo.setCardSetCode(rel.getCard_set_code());
		cardInfo.setCurrentPrice(price.getPrice());
		cardInfo.setLastUpdate(LocalDateTime.now());
		logger.info("Creating New Card Price {}", cardInfo.getCardSetCode());
		this.saveCardPriceInfo(cardInfo);
	}

	private void updateCardsNotFound(List<PriceDTO> prices) {
		for (PriceDTO price : prices) {
			GeneralSpecification<RelDeckCards> spec = new GeneralSpecification<>();
			spec.add(new SearchCriteria("cardSetCode", SearchOperation.MATCH, price.getCardSetCode()));
			List<RelDeckCards> relCards = relDeckService.findByCriterias(spec);

			if (relCards != null && relCards.size() == 1) {
				logger.info("Updating Card NOT FOUND {} - {}", price.getCardSetCode(), price.getCardRarity());			
				this.saveNewCardPrice(price, relCards);
			} else
				logger.warn(" Cannot update card price of {}", price.getCardSetCode());
		}
	}

	private List<RelDeckCards> findRelDeckByCriterias(PriceDTO price) {
		GeneralSpecification<RelDeckCards> spec = new GeneralSpecification<>();
		spec.add(new SearchCriteria("card_raridade", SearchOperation.EQUAL, price.getCardRarity()));
		spec.add(new SearchCriteria("cardSetCode", SearchOperation.MATCH, price.getCardSetCode()));
		return relDeckService.findByCriterias(spec);
	}

}
