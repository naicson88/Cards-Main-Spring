package com.naicson.yugioh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.sets.UserSetCollection;

@Repository
public interface UserSetCollectionRepository extends JpaRepository<UserSetCollection, Long>{

}
