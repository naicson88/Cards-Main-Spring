package com.naicson.yugioh.service.card;

import java.util.LinkedList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.CardYuGiOhAPI;
import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.CardAlternativeNumber;
import com.naicson.yugioh.repository.CardAlternativeNumberRepository;
import com.naicson.yugioh.repository.CardRepository;
import com.naicson.yugioh.util.GeneralFunctions;

@Service
public class CardRegistry {

	@Autowired
	CardRepository cardRepository;

	@Autowired
	CardAlternativeNumberRepository alternativeRepository;

	@Autowired
	BuildCardFromYuGiOhAPI buildCard;

	Logger logger = LoggerFactory.getLogger(CardRegistry.class);

	@Transactional
	public List<Card> registryCardFromYuGiOhAPI(List<CardYuGiOhAPI> cardsToBeRegistered) {
		
		List<Card> cardsSaved = new LinkedList<>();
		
			cardsToBeRegistered.stream().forEach(apiCard -> {
				if (!this.checkIfCardAlreadyRegisteredWithAlternativeNumber(apiCard)) {

					Card cardToBeRegistered = createCardToBeRegistered(apiCard);

					Card cardSaved = cardRepository.save(cardToBeRegistered);

					CardAlternativeNumber alternative = new CardAlternativeNumber(null, cardSaved.getId(),
							cardSaved.getNumero());

					alternativeRepository.save(alternative);

					logger.info("Card successfuly saved! {}", cardSaved.getNome());

					GeneralFunctions.saveCardInFolder(cardToBeRegistered.getNumero());

					cardsSaved.add(cardSaved);
				}
			});

		return cardsSaved;
	}

	public Card createCardToBeRegistered(CardYuGiOhAPI apiCard) {

		Card cardToBeRegistered = buildCard.createCard(apiCard);

		return cardToBeRegistered;
	}

	private boolean checkIfCardAlreadyRegisteredWithAlternativeNumber(CardYuGiOhAPI apiCard) {

		if (alternativeRepository.findByCardAlternativeNumber(apiCard.getId()) != null)
			return true;

		Card card = cardRepository.findByNome(apiCard.getName().trim());

		if (card == null || card.getId() == 0)
			return false;

		if (card.getNumero().equals(apiCard.getId()))
			throw new IllegalArgumentException("Cards with same number and diferent name can't be registered.");

		CardAlternativeNumber alternative = new CardAlternativeNumber(null, card.getId(), apiCard.getId());

		return saveAlternativerCardNumber(apiCard, alternative);
	}

	private boolean saveAlternativerCardNumber(CardYuGiOhAPI apiCard, CardAlternativeNumber alternative) {
			
			alternativeRepository.save(alternative);
			
			logger.info("Alternative card number saved!");
			GeneralFunctions.saveCardInFolder(apiCard.getId());
			return true;
	}

}
