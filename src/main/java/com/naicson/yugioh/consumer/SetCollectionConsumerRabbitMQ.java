package com.naicson.yugioh.consumer;

import cardscommons.dto.SetCollectionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;

@Component
public class SetCollectionConsumerRabbitMQ {
	
	@Autowired
	SetCollectionServiceImpl setColService;
	
	@Autowired
	ConsumerUtils consumerUtils;

	private final static String SET_COLLECTION_QUEUE = "SET_COLLECTION_QUEUE";
	
	Logger logger = LoggerFactory.getLogger(SetCollectionConsumerRabbitMQ.class);
		
	@RabbitListener(queues = SET_COLLECTION_QUEUE, autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = {Exception.class})
	public void consumerSetCollectionQueue(String json) {
			
		logger.info("Start consuming new Set Collection: {}" , json);

		setCollectionConsumer(json);

	}

	@KafkaListener(topics = SET_COLLECTION_QUEUE, groupId = "cards-main-ms")
	@Transactional(rollbackFor = { Exception.class })
	public void kafkaConsumer(String message){
		logger.info(" -> Consuming from Kafka {}", message);
		setCollectionConsumer(message);
	}

	public void setCollectionConsumer(String json) {
		SetCollectionDTO setCollection = (SetCollectionDTO) consumerUtils.convertJsonToDTO(json, JsonConverterValidationFactory.SET_COLLECTION);

		SetCollection setCollectionEntity = SetCollection.setCollectionDtoToEntity(setCollection);

		setCollectionEntity = setColService.saveSetCollection(setCollectionEntity);

		logger.info("Registered Set Collection: {}", setCollectionEntity.getName());
	}

}
