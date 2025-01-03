package com.naicson.yugioh.consumer;

import java.util.Arrays;

import cardscommons.dto.PriceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.service.card.CardPriceInformationServiceImpl;

@Component
public class PriceConsumerRabbitMQ {
	@Autowired
	ConsumerUtils consumerUtils;
	
	@Autowired
	CardPriceInformationServiceImpl priceService;

	private final static String SET_PRICE_QUEUE = "SET_PRICE_QUEUE";
	
	Logger logger = LoggerFactory.getLogger(PriceConsumerRabbitMQ.class);
	
	@RabbitListener(queues = SET_PRICE_QUEUE, autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = {Exception.class})
	public void consumer(String json) {
		logger.info("Starting Update Set Price {} ", json);
		priceConsumer(json);
	}

	@KafkaListener(topics = SET_PRICE_QUEUE, groupId = "cards-main-ms")
	@Transactional(rollbackFor = { Exception.class })
	public void kafkaConsumer(String message){
		logger.info(" -> Consuming from Kafka {}", message);
		priceConsumer(message);
	}

	private void priceConsumer(String json) {
		try {

			@SuppressWarnings("unchecked")
			PriceDTO[] prices = (PriceDTO[]) consumerUtils.convertJsonToDTO(json, JsonConverterValidationFactory.SET_PRICE);

			priceService.updateCardPrice(Arrays.asList(prices));

		} catch (ListenerExecutionFailedException e) {
			logger.error( " Error while trying consume SET_PRICE: {}", json);
		}
	}
}
