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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping({ "yugiohAPI/user-setcollection" })
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserSetCollectionController {
	
	@Autowired
	private UserSetCollectionServiceImpl service;
	
	@GetMapping("/add/{setId}")
	@Operation(summary="Add a Set Collection in Users collections", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> addSetCollectionInUsersCollection(@PathVariable("setId") Integer setId) {
		
		service.addSetCollectionInUsersCollection(setId);
		
		return new ResponseEntity<>(JSONObject.quote("SetCollection Added Successfully!"), HttpStatus.CREATED);
	}
	
	@GetMapping("/remove/{setId}")
	@Operation(summary="Remove a Set Collection in Users collections", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> removeSetCollectionInUsersCollection(@PathVariable("setId") Long setId) {
		
		service.removeSetCollectionInUsersCollection(setId);
		
		return new ResponseEntity<>(JSONObject.quote("SetCollection has been removed Successfully!"), HttpStatus.OK);
	}
	
	@GetMapping("/consult/{setId}")
	@Operation(summary="Consults DTO to User Set Collection", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<UserSetCollectionDTO> consultUserSetCollection(@PathVariable("setId") Long setId){
		
		return new ResponseEntity<>(service.consultUserSetCollection(setId), HttpStatus.OK);
	}
	
	@PostMapping("/save-set-collection")
	@Operation(summary="Save a SetCollection", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> saveSetCollection(@RequestBody UserSetCollectionDTO userCollection){
		String msg = service.saveUserSetCollection(userCollection);
		
		return new ResponseEntity<>(JSONObject.quote(msg), HttpStatus.OK);
	}
	
	@GetMapping("/setsname-by-settype/{setType}")
	@Operation(summary="Get all Decks and Sets by SetType", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<List<DeckAndSetsBySetTypeDTO>> getAllSetsBySetType(@PathVariable("setType") String setType){
		List<DeckAndSetsBySetTypeDTO> dto = service.getAllSetsBySetType(setType);
		
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
	
	@GetMapping("/set-collection-for-transfer")
	@Operation(summary="Get Set Collection sorted for transfer Cards", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<UserSetCollectionDTO> getUserSetCollectionForTransfer(@RequestParam("setId") Integer setId){
		UserSetCollectionDTO dto = service.getUserSetCollectionForTransfer(setId);
		
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
	
	@PostMapping("/save-transfer")
	@Operation(summary="Save the transfer from a Set to another", security = { @SecurityRequirement(name = "bearer-key") })
	public ResponseEntity<String> saveTransfer(@RequestBody List<UserSetCollectionDTO> setsToBeSaved ){
		String msg = service.saveTransfer(setsToBeSaved);
		
		return new ResponseEntity<>(JSONObject.quote(msg), HttpStatus.OK);
	}
	
}
