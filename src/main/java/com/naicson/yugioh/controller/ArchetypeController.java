package com.naicson.yugioh.controller;

import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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
	public ResponseEntity<TesteDTO> teste(@RequestBody TesteDTO dto){

		if(dto.getTeste().equals("bbb"))
			throw new SecurityException("Teste Retorno");

		if(dto.getTeste().equals("aaa"))
			throw new EntityNotFoundException("EntityNotFound bad request");

		Double.parseDouble(dto.getTeste());

		return new ResponseEntity<>(dto, HttpStatus.OK);
		
	}

//	@ExceptionHandler(Exception.class)
//	@ResponseStatus(HttpStatus.NOT_FOUND)
//	public ResponseEntity<String> handleNoSuchElementFoundException(Exception exception, HttpServletRequest request) {
//		System.out.println(request.getContextPath().toString());
//		System.out.println(request.getRequestURL().toString());
//		return ResponseEntity
//				.status(HttpStatus.NOT_FOUND)
//				.body(exception.getMessage());
//	}
	
	
}
