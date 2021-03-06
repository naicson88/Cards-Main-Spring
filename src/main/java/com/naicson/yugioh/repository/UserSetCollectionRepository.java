package com.naicson.yugioh.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.sets.UserSetCollection;
import com.naicson.yugioh.util.enums.SetType;

@Repository
public interface UserSetCollectionRepository extends JpaRepository<UserSetCollection, Long>{
	
	@Modifying
	@Query(value = "insert into tab_user_setcollection_deck (user_set_collection_id, deck_id) values (:setId, :deckId)",
			nativeQuery = true)
	public void saveSetUserDeckRelation(Long setId, Long deckId);
	
	@Modifying
	@Query(value = "delete from tab_user_setcollection_deck where user_set_collection_id = :setId",nativeQuery = true)		
	public void deleteSetUserDeckRelation(Long setId);
	
	public Optional<UserSetCollection> findById(Long id);

	public Page<UserSetCollection> findAllBySetCollectionType(Pageable pageable, SetType setType);
}
