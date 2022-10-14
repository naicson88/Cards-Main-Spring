package com.naicson.yugioh.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Tuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.home.HomeDTO;
import com.naicson.yugioh.data.dto.home.LastAddedDTO;
import com.naicson.yugioh.repository.HomeRepository;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.card.CardPriceInformationServiceImpl;
import com.naicson.yugioh.service.card.CardViewsInformationServiceImpl;
import com.naicson.yugioh.service.interfaces.HomeDetailService;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.SetType;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Service
public class HomeServiceImpl implements HomeDetailService {

	@Autowired
	HomeRepository homeRepository;
	@Autowired
	UserSetCollectionRepository userSetRepository;
	@Autowired
	CardPriceInformationServiceImpl cardInfoService;
	@Autowired
	CardViewsInformationServiceImpl cardViewService;

	Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);

	@Value("${sets.imgs.path}")
	private String setsImgPath;

	@Override
	public HomeDTO getHomeDto() {
		HomeDTO homeDto = new HomeDTO();
		UserDetailsImpl user = GeneralFunctions.userLogged();

		try {

			homeDto.setQtdDeck(homeRepository.returnQuantitySetType(SetType.DECK.getType(), user.getId()));
			homeDto.setQtdBoxes(homeRepository.returnQuantitySetType(SetType.BOX.getType(), user.getId()));
			homeDto.setQtdTins(homeRepository.returnQuantitySetType(SetType.TIN.getType(), user.getId()));
			homeDto.setQtdCards(homeRepository.returnQuantityCardsUserHave(user.getId()));

			homeDto.setLastSets(this.lastSetsAddedToUser());
			homeDto.setLastCards(this.lastCardsAddedToUsuer(homeRepository.lastCardsAddedToUser(user.getId())));
			homeDto.setHotNews(this.hotNews(homeRepository.getHotNews()));

			homeDto.setHighCards(this.cardInfoService.getWeeklyHighStats());
			homeDto.setLowCards(this.cardInfoService.getWeeklyLowStats());
			homeDto.setWeeklyMostView(this.cardViewService.getWeeklyMostViewed());

		} catch (ErrorMessage e) {
			e.getMessage();
		}

		return homeDto;
	}

	private List<LastAddedDTO> lastSetsAddedToUser() {

		List<LastAddedDTO> lastsDecksAdded = this.lastDecksAdded();
		List<LastAddedDTO> lastSetCollections = this.lastsSetCollectionAdded();

		List<LastAddedDTO> lastsAdded = Stream.concat(lastsDecksAdded.stream(), lastSetCollections.stream())
				.sorted(Comparator.comparing(LastAddedDTO::getRegisteredDate)).limit(10)
				.collect(Collectors.toList());

		return lastsAdded;
	}

	private Double totalDeckPrice(Long setId) {
		if (setId == null || setId == 0)
			throw new IllegalArgumentException("Invalid Set Id to get total price.");

		Double totalPrice = homeRepository.findTotalDeckPrice(setId);

		return totalPrice;

	}

	private Double totalSetCollectionPrice(List<Long> deckOfSetCollectionId) {

		if (deckOfSetCollectionId == null || deckOfSetCollectionId.size() == 0)
			return 0.0;

		Double totalPrice = deckOfSetCollectionId.stream()
				.mapToDouble(deckId -> homeRepository.findTotalSetPrice(deckId)).sum();

		return totalPrice;
	}

	private List<LastAddedDTO> lastCardsAddedToUsuer(List<Tuple> lastCardsAddedTuple) {
		List<LastAddedDTO> lastCardsAdded = new ArrayList<>();

		if (lastCardsAddedTuple != null && !lastCardsAddedTuple.isEmpty()) {

			lastCardsAdded = lastCardsAddedTuple.stream().map(card -> {
				LastAddedDTO lastCard = new LastAddedDTO();
				lastCard.setCardNumber(card.get(0, Integer.class).longValue());
				lastCard.setName(card.get(1, String.class));
				lastCard.setSetCode(card.get(2, String.class));
				lastCard.setPrice(card.get(3, Double.class));

				return lastCard;
			}).collect(Collectors.toList());

		} else {
			return Collections.emptyList();
		}

		return lastCardsAdded;
	}

	private List<LastAddedDTO> hotNews(List<Tuple> hotNews) {
		List<LastAddedDTO> hotNewsList = new ArrayList<>();

		if (hotNews != null && !hotNews.isEmpty()) {
			hotNewsList = hotNews.stream().map(set -> {
				LastAddedDTO lastAdded = new LastAddedDTO();

				lastAdded.setId(set.get(0, BigInteger.class).longValue());
				lastAdded.setImg(set.get(2, String.class));
				lastAdded.setName(set.get(1, String.class));
				lastAdded.setSetType(set.get(4, String.class));
				
				return lastAdded;
			}).collect(Collectors.toList());

		} else 
			throw new NoSuchElementException("Hot News list is empty");
		
		return hotNewsList;
	}
	
	private List<LastAddedDTO> lastDecksAdded() {
		 UserDetailsImpl user = GeneralFunctions.userLogged();

		List<Tuple> sets = homeRepository.returnLastDecksAddedToUser(user.getId());
		List<LastAddedDTO> lastDecksAdded = new ArrayList<>();

		if (sets != null && !sets.isEmpty()) {
			// Lasts cards or decks added
			lastDecksAdded = sets.stream().map(set -> {

				LastAddedDTO lastSet = new LastAddedDTO();

				lastSet.setId(set.get(0, BigInteger.class).longValue());
				lastSet.setName(set.get(2, String.class));
				lastSet.setImg(set.get(1, String.class));
				lastSet.setPrice(totalDeckPrice(lastSet.getId()));
				lastSet.setRegisteredDate(set.get(5, Date.class));
				lastSet.setSetType("DECK");
				return lastSet;
			}).collect(Collectors.toList());
			
		} else {
			return Collections.emptyList();
		}

		return lastDecksAdded;
	}

	private List<LastAddedDTO> lastsSetCollectionAdded() {
	    UserDetailsImpl user = GeneralFunctions.userLogged();

		List<Tuple> sets = homeRepository.returnLastSetsAddedToUser(user.getId());
		List<LastAddedDTO> lastSetsAdded = new ArrayList<>();

		if (!sets.isEmpty()) {

				lastSetsAdded = sets.stream().map(set -> {

				LastAddedDTO lastSet = new LastAddedDTO();

				lastSet.setId(set.get(0, BigInteger.class).longValue());
				lastSet.setName(set.get(5, String.class));
				lastSet.setImg(set.get(3, String.class));
				lastSet.setPrice(totalSetCollectionPrice(userSetRepository.consultSetUserDeckRelation(lastSet.getId())));						
				lastSet.setRegisteredDate(set.get(8, Date.class));
				lastSet.setSetType(set.get(10, String.class));

				return lastSet;
			}).collect(Collectors.toList());

			if (lastSetsAdded == null || lastSetsAdded.isEmpty()) 
				throw new ErrorMessage("List with lasts added is empty");
		} else {
			return Collections.emptyList();
		}

		return lastSetsAdded;
	}

}
