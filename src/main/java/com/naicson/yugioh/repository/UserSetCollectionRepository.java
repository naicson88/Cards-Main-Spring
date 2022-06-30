package com.naicson.yugioh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.sets.UserSetCollection;

@Repository
public interface UserSetCollectionRepository extends JpaRepository<UserSetCollection, Long>{
	
	@Modifying
	@Query(value = "insert into tab_user_setcollection_deck (user_set_collection_id, deck_id) values (:setId, :deckId)",
			nativeQuery = true)
	public void saveSetUserDeckRelation(Long setId, Long deckId);
}
