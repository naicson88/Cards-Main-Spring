package com.naicson.yugioh.service.card;

import cardscommons.dto.CardYuGiOhAPI;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.CardAlternativeNumber;
import com.naicson.yugioh.repository.CardAlternativeNumberRepository;
import com.naicson.yugioh.repository.CardRepository;
import com.naicson.yugioh.util.GeneralFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class CardRegistry {

	@Autowired
	CardRepository cardRepository;

	@Autowired
	CardAlternativeNumberRepository alternativeRepository;

	@Autowired
	BuildCardFromYuGiOhAPI buildCard;

	Logger logger = LoggerFactory.getLogger(CardRegistry.class);
	
	private static final String CARD_FOLDER = "C:\\Cards\\";
	private static final String CROPPED_CARD_FOLDER = "C:\\Cropped\\";
	
	@Value("${yugioh.api.url.img}")
	private String imgCardUrl;
	@Value("${yugioh.api.url.img.cropped}")
	private String imgCroppedCardUrl;
	


	@Transactional
	public List<Card> registryCardFromYuGiOhAPI(List<CardYuGiOhAPI> cardsToBeRegistered) {
		
		if(cardsToBeRegistered == null)
			return Collections.emptyList();
		
		List<Card> cardsSaved = new LinkedList<>();
		
			cardsToBeRegistered.stream().forEach(apiCard -> {
				if (!this.checkIfCardAlreadyRegisteredWithAlternativeNumber(apiCard)) {

					Card cardToBeRegistered = createCardToBeRegistered(apiCard);

					Card cardSaved = cardRepository.save(cardToBeRegistered);

					CardAlternativeNumber alternative = new CardAlternativeNumber(cardSaved.getId(),
							cardSaved.getNumero());

					alternativeRepository.save(alternative);

					logger.info("Card successfuly saved! {}", cardSaved.getNome());

					GeneralFunctions.saveCardInFolder(cardToBeRegistered.getNumero(), imgCardUrl, CARD_FOLDER);
					GeneralFunctions.saveCardInFolder(cardToBeRegistered.getNumero(), imgCroppedCardUrl, CROPPED_CARD_FOLDER);

					cardsSaved.add(cardSaved);
				}
			});

		return cardsSaved;
	}

	public Card createCardToBeRegistered(CardYuGiOhAPI apiCard) {
		return buildCard.createCard(apiCard);

	}

	private boolean checkIfCardAlreadyRegisteredWithAlternativeNumber(CardYuGiOhAPI apiCard) {

		if (alternativeRepository.findByCardAlternativeNumber(apiCard.getId()) != null)
			return true;

		Card card = cardRepository.findByNome(apiCard.getName().trim());

		if (card == null || card.getId() == 0)
			return false;

		if (card.getNumero().equals(apiCard.getId()))
			throw new IllegalArgumentException("Cards with same number and diferent name can't be registered.");

		CardAlternativeNumber alternative = new CardAlternativeNumber(card.getId(), apiCard.getId());

		return saveAlternativerCardNumber(apiCard, alternative);
	}

	private boolean saveAlternativerCardNumber(CardYuGiOhAPI apiCard, CardAlternativeNumber alternative) {
			
			alternativeRepository.save(alternative);
			
			logger.info("Alternative card number saved!");
			GeneralFunctions.saveCardInFolder(apiCard.getId(), imgCardUrl, CARD_FOLDER);
			GeneralFunctions.saveCardInFolder(apiCard.getId(), imgCroppedCardUrl, CROPPED_CARD_FOLDER);
			return true;
	}

}
