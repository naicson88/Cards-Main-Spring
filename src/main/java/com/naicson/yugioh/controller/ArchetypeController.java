package com.naicson.yugioh.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.entity.Archetype;
import com.naicson.yugioh.service.ArchetypeServiceImpl;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({"yugiohAPI/arch"})
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class ArchetypeController {
	
	@Autowired
	ArchetypeServiceImpl archetypeService;
	
	
	
	@Cacheable("archetypes")
	@ApiOperation(value="Return all Archetypes", authorizations = { @Authorization(value="JWT") })
	@GetMapping("/all")
	public List<Archetype> getAllArchetypes(){	
		return archetypeService.getAllArchetypes();
	}
	
	@ApiOperation(value="Return a single Archetype by ID.", authorizations = { @Authorization(value="JWT") })
	@GetMapping("/archetype/{archId}")
	public Archetype getByArchetypeId(@PathVariable("archId") Integer archId) {		
		return archetypeService.getByArchetypeId(archId);
	}
	
	
}
