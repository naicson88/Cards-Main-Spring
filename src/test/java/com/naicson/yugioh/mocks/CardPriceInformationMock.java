package com.naicson.yugioh.mocks;

import java.time.LocalDateTime;

import com.naicson.yugioh.entity.stats.CardPriceInformation;

public class CardPriceInformationMock {
	
	public static CardPriceInformation generateValidCardPriceInfo(Long id) {
		
		CardPriceInformation info = new CardPriceInformation();
		info.setCardId(123);
		info.setCardNumber(6506006L);
		info.setCardSetCode("AAA-123");
		info.setCurrentPrice(10.99);
		info.setId(id);
		info.setLastUpdate(LocalDateTime.now());
		info.setPrice2(1.0);
		info.setPrice3(2.0);
		info.setPrice4(4.0);
		info.setPrice5(5.0);
		info.setWeeklyPercentVariable(5.5);
		
		return info;
	}
}
