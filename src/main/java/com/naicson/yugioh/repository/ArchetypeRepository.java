package com.naicson.yugioh.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.naicson.yugioh.entity.Archetype;

import javax.persistence.Tuple;


@Repository
public interface ArchetypeRepository extends JpaRepository<Archetype, Integer> {

	Optional<Archetype> findById(Integer archId);
	Archetype findByArcName(String archetype);

	@Query(value = "SELECT DISTINCT LEFT(arc_name, 1) FROM tab_archetypes;", nativeQuery = true)
	List<Tuple> getFirstLetterAllArchetypes();



}
