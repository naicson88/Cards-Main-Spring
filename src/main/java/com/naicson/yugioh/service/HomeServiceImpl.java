package com.naicson.yugioh.service;

import com.naicson.yugioh.data.dto.home.GeneralSearchDTO;
import com.naicson.yugioh.data.dto.home.HomeDTO;
import com.naicson.yugioh.data.dto.home.LastAddedDTO;
import com.naicson.yugioh.data.factory.LastAddedDTOFactory;
import com.naicson.yugioh.data.factory.LastAddedDTOTypes;
import com.naicson.yugioh.repository.HomeRepository;
import com.naicson.yugioh.repository.UserSetCollectionRepository;
import com.naicson.yugioh.service.card.CardPriceInformationServiceImpl;
import com.naicson.yugioh.service.card.CardViewsInformationServiceImpl;
import com.naicson.yugioh.service.interfaces.HomeDetailService;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.GeneralFunctions;
import com.naicson.yugioh.util.enums.SetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	
	 @Autowired
	 CacheManager cacheManager;
	
	@Value("${yugioh.api.url.img}")
	private String yugiohAPIUrlImg;

	Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);

	@Value("${sets.imgs.path}")
	private String setsImgPath;

	@Override
	public HomeDTO getHomeDto() {
		logger.info(" -> Getting Home Info...");
		HomeDTO homeDto = new HomeDTO();
		UserDetailsImpl user = GeneralFunctions.userLogged();

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

		return homeDto;
	}

	private List<LastAddedDTO> lastSetsAddedToUser() {

		List<LastAddedDTO> lastsDecksAdded = this.lastDecksAdded();
		List<LastAddedDTO> lastSetCollections = this.lastsSetCollectionAdded();

		return Stream.concat(lastsDecksAdded.stream(), lastSetCollections.stream())
				.sorted(Comparator.comparing(LastAddedDTO::getRegisteredDate).reversed()).limit(10)
				.toList();
	}

	private Double totalDeckPrice(Long setId) {
		if (setId == null || setId == 0)
			throw new IllegalArgumentException("Invalid Set Id to get total price.");

		return homeRepository.findTotalDeckPrice(setId);
	}

	private Double totalSetCollectionPrice(List<Long> deckOfSetCollectionId) {

		if (deckOfSetCollectionId == null || deckOfSetCollectionId.isEmpty())
			return 0.0;

		return deckOfSetCollectionId.stream()
				.mapToDouble(deckId -> homeRepository.findTotalSetPrice(deckId)).sum();

	}

	private List<LastAddedDTO> lastCardsAddedToUsuer(List<Tuple> lastCardsAddedTuple) {

		if (lastCardsAddedTuple == null || lastCardsAddedTuple.isEmpty()) 
			return Collections.emptyList();
		
		return LastAddedDTOFactory.createDto(lastCardsAddedTuple, LastAddedDTOTypes.LAST_CARDS_ADDED_TO_USER);
	}

	private List<LastAddedDTO> hotNews(List<Tuple> hotNews) {

		if (hotNews == null || hotNews.isEmpty())
			throw new NoSuchElementException("Hot News list is empty");
		
		return LastAddedDTOFactory.createDto(hotNews, LastAddedDTOTypes.HOT_NEWS);
		
	}
	
	private List<LastAddedDTO> lastDecksAdded() {

		List<Tuple> sets = homeRepository.returnLastDecksAddedToUser(GeneralFunctions.userLogged().getId());

		if (sets == null || sets.isEmpty())
			return Collections.emptyList();

		return  LastAddedDTOFactory.createDto(sets, LastAddedDTOTypes.LAST_DECK_ADDED);
	}

	private List<LastAddedDTO> lastsSetCollectionAdded() {

		List<Tuple> sets = homeRepository.returnLastSetsAddedToUser(GeneralFunctions.userLogged().getId());

		if (sets == null || sets.isEmpty()) 
			return Collections.emptyList();
		
		return  LastAddedDTOFactory.createDto(sets, LastAddedDTOTypes.LAST_SET_COLLECTION_ADDED);
		
	}
	
	public List<GeneralSearchDTO> retrieveSearchedData(final String param, List<GeneralSearchDTO> dto){		
		return dto.stream()
				.filter( data -> data.getName().toLowerCase().contains(param.toLowerCase()))
				.collect((Collectors.toList()));
	}
	
	@Cacheable("generalSearch")
	public List<GeneralSearchDTO> getGeneralData() {	
		return homeRepository.generalSearch().stream().map(data -> {
			
			GeneralSearchDTO dto = new GeneralSearchDTO(
					data.get(0, BigInteger.class).longValue(),
					data.get(1, String.class),
					data.get(4, String.class),
					data.get(2, String.class),
					data.get(3, String.class)
					);
			
			if(dto.getEntityType().equals("CARD"))
				dto.setImg(yugiohAPIUrlImg+dto.getImg()+".jpg");
			
			return dto;
			
		}).toList();
	
	}

}
