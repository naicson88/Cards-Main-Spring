package com.naicson.yugioh.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.data.dto.home.GeneralSearchDTO;
import com.naicson.yugioh.data.dto.home.HomeDTO;
import com.naicson.yugioh.service.HomeServiceImpl;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@RequestMapping({"yugiohAPI/home"})
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class HomeController {
	
	@Autowired
	HomeServiceImpl homeService;
	
	@GetMapping(path = "/info")
	@ApiOperation(value="Get informations of Home", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<HomeDTO> homeInformations(){
		
		HomeDTO home = homeService.getHomeDto();
		
		return new ResponseEntity<>(home, HttpStatus.OK);
	}
	
	@GetMapping("/general-search")
	@ApiOperation(value="Return Search by Param", authorizations = { @Authorization(value="JWT") })
	public ResponseEntity<List<GeneralSearchDTO>> getEntitiesByParam(@RequestParam String param){
		//Need to be here fot Cache work!!
		List<GeneralSearchDTO> dto = homeService.getGeneralData(); 
		List<GeneralSearchDTO> listDto = homeService.retrieveSearchedData(param, dto);
		
		return new ResponseEntity<>(listDto, HttpStatus.OK);
	}
}
