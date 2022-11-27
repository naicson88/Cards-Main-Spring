package com.naicson.yugioh.repository;

import java.util.List;

import javax.persistence.Tuple;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.sets.SetCollection;

@Repository
public interface SetCollectionRepository extends JpaRepository<SetCollection, Integer>{
	
	@Query(value = "SELECT * FROM yugioh.tab_set_collection where set_collection_type = :setType order by release_date desc",
			countQuery = "SELECT count(*) FROM yugioh.tab_set_collection", nativeQuery = true)
	public Page<SetCollection> findAllBySetType(Pageable pageable, String setType);
	
	@Query(value = "select usc.id, usc.name FROM yugioh.tab_set_collection usc where set_collection_type = :setType order by id desc", 
			nativeQuery = true)
	public List<Tuple> getAllSetsBySetType(String setType);
	
	@Modifying
	@Query(value = "insert into tab_setcollection_deck (set_collection_id, deck_id) values (:setId, :deckId)",
			nativeQuery = true)
	public void saveSetDeckRelation(Long setId, Long deckId);
	
	@Query(value = "select deck_id from tab_setcollection_deck where set_collection_id = :setId",nativeQuery = true)	
	public List<Long> getSetDeckRelationId(Integer setId);


}
