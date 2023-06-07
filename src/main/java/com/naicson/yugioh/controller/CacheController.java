package com.naicson.yugioh.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"yugiohAPI/cache"})
@CrossOrigin(origins = "*", maxAge = 3600)
public class CacheController {
	
	@Autowired
	CacheManager cacheManager;
	
	Logger logger = LoggerFactory.getLogger(CacheController.class);
	
	@CacheEvict(value = "archetypes", allEntries = true)
	@GetMapping("/archetypes")
	public ResponseEntity<String> evictArchetypes() {
		logger.info("Cleaning Archetypes cache...");
		return new ResponseEntity<>("Archetype cache was clear!", HttpStatus.OK);
	}
	
	@GetMapping("/evict-all")
	public void evictAllCaches() {
	    cacheManager.getCacheNames().stream()
	      .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
	}
	
	@Scheduled(cron="0 0 0 * * ?")
	public void evictAllcachesAtIntervals() {		
	    evictAllCaches();
	}
	
}
