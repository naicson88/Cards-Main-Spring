package com.naicson.yugioh.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.CardAlternativeNumber;

@Repository
public interface CardAlternativeNumberRepository extends JpaRepository<CardAlternativeNumber, Integer> {

	CardAlternativeNumber findByCardAlternativeNumber(Long id);

	List<CardAlternativeNumber> findAllByCardId(Integer id);


}
