package com.naicson.yugioh.repository;

import com.naicson.yugioh.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LogEntityRepository extends JpaRepository<LogEntity, UUID> {
}
