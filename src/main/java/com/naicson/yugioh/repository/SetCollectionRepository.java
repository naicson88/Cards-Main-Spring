package com.naicson.yugioh.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.sets.SetCollection;

@Repository
public interface SetCollectionRepository extends JpaRepository<SetCollection, Integer>{
	
	@Query(value = "SELECT * FROM yugioh.tab_set_collection where set_collection_type = :setType",
			countQuery = "SELECT count(*) FROM yugioh.tab_set_collection", nativeQuery = true)
	public Page<SetCollection> findAllBySetType(Pageable pageable, String setType);


}
