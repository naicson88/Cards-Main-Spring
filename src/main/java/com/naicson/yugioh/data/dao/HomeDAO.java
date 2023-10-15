package com.naicson.yugioh.data.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class HomeDAO {
	
	@PersistenceContext
	 EntityManager em;
}
