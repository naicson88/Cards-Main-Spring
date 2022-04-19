package com.naicson.yugioh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.sets.SetCollection;

@Repository
public interface SetCollectionRepository extends JpaRepository<SetCollection, Integer>{

}
