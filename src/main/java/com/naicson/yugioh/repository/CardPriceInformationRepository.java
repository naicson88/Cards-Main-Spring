package com.naicson.yugioh.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.stats.CardPriceInformation;

@Repository
public interface CardPriceInformationRepository extends JpaRepository<CardPriceInformation, Long> {

	List<CardPriceInformation> findTop6ByOrderByWeeklyPercentVariableDesc();
	List<CardPriceInformation> findTop6ByOrderByWeeklyPercentVariableAsc();
	Optional<List<CardPriceInformation>> findByCardId(Integer cardId);
	List<CardPriceInformation> findByCardSetCode(String cardSetCode);
	

}
