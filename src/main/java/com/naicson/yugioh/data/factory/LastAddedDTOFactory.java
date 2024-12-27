package com.naicson.yugioh.data.factory;

import com.naicson.yugioh.data.dto.home.LastAddedDTO;
import com.naicson.yugioh.util.enums.SetType;

import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class LastAddedDTOFactory {

    private LastAddedDTOFactory() {}

    public static List<LastAddedDTO> createDto(List<Tuple> tupleList, LastAddedDTOTypes type ){

        if(type == LastAddedDTOTypes.LAST_CARDS_ADDED_TO_USER){
             return tupleList.stream().map(card -> {
                    LastAddedDTO lastCard = new LastAddedDTO();
                    lastCard.setCardNumber(card.get(0, Integer.class).longValue());
                    lastCard.setName(card.get(1, String.class));
                    lastCard.setSetCode(card.get(2, String.class));
                    lastCard.setPrice(card.get(3, Double.class));
                    return lastCard;
                }).toList();
         }
        else if(type == LastAddedDTOTypes.LAST_DECK_ADDED){

            return tupleList.stream().map(set -> {
                LastAddedDTO lastSet = new LastAddedDTO();
                lastSet.setId(set.get(0, BigInteger.class).longValue());
                lastSet.setName(set.get(2, String.class));
                lastSet.setImg(set.get(1, String.class));
               // TENTAR COLOCAR ISSO NA QUERY DO TUPLE
              //  lastSet.setPrice(totalDeckPrice(lastSet.getId())); TENTAR COLOCAR ISSO NA QUERY DO TUPLE
                lastSet.setRegisteredDate(set.get(5, Date.class));
                lastSet.setSetType(SetType.DECK);
                return lastSet;
            }).toList();
        }
        else if(type == LastAddedDTOTypes.LAST_SET_COLLECTION_ADDED){
            return tupleList.stream().map(set -> {
                LastAddedDTO lastSet = new LastAddedDTO();
                lastSet.setId(set.get(0, BigInteger.class).longValue());
                lastSet.setName(set.get(5, String.class));
                lastSet.setImg(set.get(3, String.class));
                // TENTAR COLOCAR ISSO NA QUERY DO TUPLE
               // lastSet.setPrice(totalSetCollectionPrice(userSetRepository.consultSetUserDeckRelation(lastSet.getId())));
                lastSet.setRegisteredDate(set.get(8, Date.class));
                lastSet.setSetType(set.get(10, SetType.class));
                return lastSet;
            }).toList();

        }
        else if(type == LastAddedDTOTypes.HOT_NEWS){
          return  tupleList.stream().map(set -> {
                LastAddedDTO lastAdded = new LastAddedDTO();
                lastAdded.setId(set.get(0, BigInteger.class).longValue());
                lastAdded.setImg(set.get(2, String.class));
                lastAdded.setName(set.get(1, String.class));
                lastAdded.setSetType(set.get(4, SetType.class));
                return lastAdded;
            }).toList();
        }

        return Collections.emptyList();
    }
}


