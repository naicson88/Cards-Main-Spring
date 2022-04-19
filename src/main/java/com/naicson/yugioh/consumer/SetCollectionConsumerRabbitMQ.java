package com.naicson.yugioh.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naicson.yugioh.data.dto.set.SetCollectionDto;

@Component
public class SetCollectionConsumerRabbitMQ {
	
	Logger logger = LoggerFactory.getLogger(DeckConsumerRabbitMQ.class);
		
	@RabbitListener(queues = "${rabbitmq.queue.setcollection}")
	private void consumerSetCollectionQueue(String json) {
		try {	
			System.out.println(json);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
			
			SetCollectionDto dto = mapper.readValue(json, SetCollectionDto.class);
			System.out.println(dto);
			
		}catch (Exception e) {
			logger.error("Error consuming Set Collection {} " , json);
		}
	}
}
