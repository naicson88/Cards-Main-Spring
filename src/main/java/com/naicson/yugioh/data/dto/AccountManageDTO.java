package com.naicson.yugioh.data.dto;

import com.naicson.yugioh.entity.auth.User;

public record AccountManageDTO(
	
	 String username,
	 String pass,
	 String newpass,
	 String email,
	 String newemail
	 ) {};


