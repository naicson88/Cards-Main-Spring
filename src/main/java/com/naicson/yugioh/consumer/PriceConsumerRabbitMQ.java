package com.naicson.yugioh.consumer;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.data.dto.PriceDTO;
import com.naicson.yugioh.service.card.CardPriceInformationServiceImpl;

@Component
public class PriceConsumerRabbitMQ {
	
	@Autowired
	ConsumerUtils consumerUtils;
	
	@Autowired
	CardPriceInformationServiceImpl priceService;
	
	Logger logger = LoggerFactory.getLogger(PriceConsumerRabbitMQ.class);
	
	@RabbitListener(queues = "${rabbitmq.queue.setprice}", autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = {Exception.class})
	public void consumer(String json) {
		
		try {
			logger.info("Starting Update Set Price {} ", json);
			
			@SuppressWarnings("unchecked")
			PriceDTO[] prices = (PriceDTO[]) consumerUtils.convertJsonToDTO(json, JsonConverterValidationFactory.SET_PRICE);
			
			priceService.updateCardPrice(Arrays.asList(prices));
			
		} catch (ListenerExecutionFailedException e) {
			logger.error( "Error while trying consume SET_PRICE: {}", json);
		}

	}
}
