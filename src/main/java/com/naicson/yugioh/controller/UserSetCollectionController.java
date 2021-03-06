package com.naicson.yugioh.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.service.setcollection.UserSetCollectionServiceImpl;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({ "yugiohAPI/user-setcollection" })
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserSetCollectionController {
	
	@Autowired
	private UserSetCollectionServiceImpl service;
	
	@GetMapping("/add/{setId}")
	@ApiOperation(value="Add a Set Collection in Users collections", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> addSetCollectionInUsersCollection(@PathVariable("setId") Integer setId) {
		
		service.addSetCollectionInUsersCollection(setId);
		
		return new ResponseEntity<String>("SetCollection Added Successfully!", HttpStatus.CREATED);
	}
	
	@GetMapping("/remove/{setId}")
	@ApiOperation(value="Remove a Set Collection in Users collections", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> removeSetCollectionInUsersCollection(@PathVariable("setId") Long setId) {
		
		service.removeSetCollectionInUsersCollection(setId);
		
		return new ResponseEntity<String>("SetCollection has been removed Successfully!", HttpStatus.CREATED);
	}
}
