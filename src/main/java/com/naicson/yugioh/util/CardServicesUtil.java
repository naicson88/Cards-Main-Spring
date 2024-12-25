package com.naicson.yugioh.util;

import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
public class CardServicesUtil {
    @Autowired
    private CardRepository cardRepository;

    public Card cardServiceFindByNumero(Long cardNumber) {
        return cardRepository.findByNumero(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find Card with number: "+ cardNumber));
    }

    public Card cardServiceFindById(Integer id){
        return cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find Card with id: "+ id));
    }



}
