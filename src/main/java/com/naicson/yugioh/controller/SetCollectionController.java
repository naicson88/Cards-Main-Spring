package com.naicson.yugioh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.entity.sets.SetCollection;
import com.naicson.yugioh.service.interfaces.SetCollectionService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({ "yugiohAPI/collection" })
@CrossOrigin(origins = "*", maxAge = 3600)
public class SetCollectionController {
	
	@Autowired
	SetCollectionService service;
	
	@GetMapping("/{id}")
	@ApiOperation(value="Get a Set Collection by its ID", authorizations = { @Authorization(value="JWT") })
	public SetCollection findById(@PathVariable("id") Integer id) {
		return service.findById(id);
	}
	

}
