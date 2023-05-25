package com.naicson.yugioh.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.data.dto.home.GeneralSearchDTO;
import com.naicson.yugioh.data.dto.home.HomeDTO;
import com.naicson.yugioh.service.HomeServiceImpl;

@RestController
@RequestMapping({"yugiohAPI/home"})
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class HomeController {
	
	@Autowired
	HomeServiceImpl homeService;
	
	@GetMapping(path = "/info")
	public ResponseEntity<HomeDTO> homeInformations(){
		HomeDTO home = homeService.getHomeDto();
		
		return new ResponseEntity<>(home, HttpStatus.OK);
	}
	
	@GetMapping("/general-search")
	@Cacheable("generalSearch")
	public ResponseEntity<List<GeneralSearchDTO>> getEntitiesByParam(){
		
		List<GeneralSearchDTO> listDto = homeService.getEntitiesByParam();
		
		return new ResponseEntity<>(listDto, HttpStatus.OK);
	}
}
