package com.naicson.yugioh.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.naicson.yugioh.data.composite.JsonConverterValidationFactory;
import com.naicson.yugioh.data.dto.set.SetCollectionDto;
import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.service.setcollection.SetCollectionServiceImpl;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Component
public class SetCollectionConsumerRabbitMQ {
	
	@Autowired
	SetCollectionServiceImpl setColService;
	
	@Autowired
	ConsumerUtils consumerUtils;
	
	Logger logger = LoggerFactory.getLogger(SetCollectionConsumerRabbitMQ.class);
		
	@RabbitListener(queues = "${rabbitmq.queue.setcollection}", autoStartup = "${rabbitmq.autostart.consumer}")
	@Transactional(rollbackFor = {Exception.class, ErrorMessage.class})
	public void consumerSetCollectionQueue(String json) {
			
		logger.info("Start consuming new Set Collection: {}" , json);
		
		SetCollectionDto setCollection = (SetCollectionDto) consumerUtils.convertJsonToDTO(json, JsonConverterValidationFactory.SET_COLLECTION);
		
		SetCollection setCollectionEntity = SetCollection.setCollectionDtoToEntity(setCollection);
	
		setCollectionEntity = setColService.saveSetCollection(setCollectionEntity);
			
		logger.info("Registered Set Collection: {}", setCollectionEntity.getName());			
			
	}

}
