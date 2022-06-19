package com.naicson.yugioh.service.interfaces;

import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.home.HomeDTO;

@Service
public interface HomeDetailService {
	
	HomeDTO getHomeDto();
}
