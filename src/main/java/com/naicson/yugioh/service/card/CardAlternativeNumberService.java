package com.naicson.yugioh.service.card;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.CardAlternativeNumber;
import com.naicson.yugioh.repository.CardAlternativeNumberRepository;
import com.naicson.yugioh.service.deck.DeckServiceImpl;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Service
public class CardAlternativeNumberService {
	
	@Autowired
	CardAlternativeNumberRepository repository;
	@Value("${yugioh.api.url.img.cropped}")
	private String url;
	
	Logger LOGGER = LoggerFactory.getLogger(CardAlternativeNumberService.class);
	
	public Card findCardByCardNumber(Long cardNumber) {
		return repository.findCardByCardNumber(cardNumber);
	}
	
	public List<CardAlternativeNumber> findAllByCardId(Integer cardId) {
		return repository.findAllByCardId(cardId);
	}
	
	public CardAlternativeNumber findByCardAlternativeNumber(Long cardNumber) {
		return repository.findByCardAlternativeNumber(cardNumber);
	}
	
	@Transactional(rollbackFor = Exception.class)
	public CardAlternativeNumber save(CardAlternativeNumber card) {
		return repository.save(card);
	}
	
	public void saveCardArtCropped() {
		List<CardAlternativeNumber> listCards = repository.findAll();
		
		for(CardAlternativeNumber card : listCards) {
			try (InputStream in = new URL(url + card.getCardAlternativeNumber() + ".jpg")
					.openStream()) {
				
				Files.copy(in, Paths.get("C:\\Cropped\\" + card.getCardAlternativeNumber() + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
				
				Thread.sleep(500);
//				try {			
//					
//					
//				} catch (FileAlreadyExistsException e) {
//					LOGGER.warn(e.getLocalizedMessage());
//					String randomString = RandomStringUtils.randomAlphabetic(10);
//					Files.copy(in, Paths.get("C:\\Cropped\\" + card.getCardAlternativeNumber() +"-"+randomString+".jpg"));
//				} catch (Exception e) {
//					LOGGER.error("Error when saving cropped card: {}", card.getCardAlternativeNumber());
//				}

			} catch (IOException e) {
				LOGGER.error("Error when saving cropped card: {}", card.getCardAlternativeNumber());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
