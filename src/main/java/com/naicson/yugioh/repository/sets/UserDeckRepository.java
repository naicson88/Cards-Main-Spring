package com.naicson.yugioh.repository.sets;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.sets.UserDeck;

@Repository
public interface UserDeckRepository extends JpaRepository<UserDeck, Long>{
	
	Page<UserDeck> findAllByUserIdAndSetType(long userId, String setType, Pageable pageable);

	List<UserDeck> findTop30ByNomeContaining(String setName);

	Page<UserDeck> findAllBySetType(Pageable pageable, String setType);

}
