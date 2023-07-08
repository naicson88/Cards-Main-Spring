package com.naicson.yugioh.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.Card;
import com.naicson.yugioh.entity.CardAlternativeNumber;

@Repository
public interface CardAlternativeNumberRepository extends JpaRepository<CardAlternativeNumber, Integer> {

	CardAlternativeNumber findByCardAlternativeNumber(Long cardNumber);

	List<CardAlternativeNumber> findAllByCardId(Integer id);
	
	@Query(value = "select * from tab_cards where id = (select card_id from tab_card_alternative_numbers where  card_alternative_number  = :cardNumber)"
			, nativeQuery = true)
	Optional<Card> findCardByCardNumber(Long cardNumber);

}
