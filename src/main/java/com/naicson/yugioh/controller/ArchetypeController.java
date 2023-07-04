package com.naicson.yugioh.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.entity.Archetype;
import com.naicson.yugioh.entity.TesteDTO;
import com.naicson.yugioh.service.ArchetypeServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping({"yugiohAPI/arch"})
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class ArchetypeController {
	
	@Autowired
	ArchetypeServiceImpl archetypeService;
	
	@Cacheable("archetypes")
	@Operation(summary="Return all Archetypes", security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping("/all")
	public List<Archetype> getAllArchetypes(){	
		return archetypeService.getAllArchetypes();
	}
	
	@Operation(summary="Return a single Archetype by ID.", security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping("/archetype/{archId}")
	public Archetype getByArchetypeId(@PathVariable("archId") Integer archId) {	
		return archetypeService.getByArchetypeId(archId);
	}
	
	@PostMapping("/teste")
	public ResponseEntity<TesteDTO> teste(@RequestBody TesteDTO dto, HttpServletRequest request){	
		
		return new ResponseEntity<>(dto, HttpStatus.OK);
		
	}
	
	
}
