package com.naicson.yugioh.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.data.dto.set.DeckAndSetsBySetTypeDTO;
import com.naicson.yugioh.data.dto.set.UserSetCollectionDTO;
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
		
		return new ResponseEntity<>(JSONObject.quote("SetCollection Added Successfully!"), HttpStatus.CREATED);
	}
	
	@GetMapping("/remove/{setId}")
	@ApiOperation(value="Remove a Set Collection in Users collections", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> removeSetCollectionInUsersCollection(@PathVariable("setId") Long setId) {
		
		service.removeSetCollectionInUsersCollection(setId);
		
		return new ResponseEntity<>(JSONObject.quote("SetCollection has been removed Successfully!"), HttpStatus.OK);
	}
	
	@GetMapping("/consult/{setId}")
	@ApiOperation(value="Consults DTO to User Set Collection", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<UserSetCollectionDTO> consultUserSetCollection(@PathVariable("setId") Long setId){
		
		return new ResponseEntity<>(service.consultUserSetCollection(setId), HttpStatus.OK);
	}
	
	@PostMapping("/save-set-collection")
	@ApiOperation(value="Save a SetCollection", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> saveSetCollection(@RequestBody UserSetCollectionDTO userCollection){
		String msg = service.saveUserSetCollection(userCollection);
		
		return new ResponseEntity<>(JSONObject.quote(msg), HttpStatus.OK);
	}
	
	@GetMapping("/setsname-by-settype/{setType}")
	@ApiOperation(value="Get all Decks and Sets by SetType", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<DeckAndSetsBySetTypeDTO>> getAllSetsBySetType(@PathVariable("setType") String setType){
		List<DeckAndSetsBySetTypeDTO> dto = service.getAllSetsBySetType(setType);
		
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
	
	@GetMapping("/set-collection-for-transfer")
	@ApiOperation(value="Get Set Collection sorted for transfer Cards", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<UserSetCollectionDTO> getUserSetCollectionForTransfer(@RequestParam("setId") Integer setId){
		UserSetCollectionDTO dto = service.getUserSetCollectionForTransfer(setId);
		
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
	
	@PostMapping("/save-transfer")
	@ApiOperation(value="Save the transfer from a Set to another", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<String> saveTransfer(@RequestBody List<UserSetCollectionDTO> setsToBeSaved ){
		String msg = service.saveTransfer(setsToBeSaved);
		
		return new ResponseEntity<>(JSONObject.quote(msg), HttpStatus.OK);
	}
	
}
