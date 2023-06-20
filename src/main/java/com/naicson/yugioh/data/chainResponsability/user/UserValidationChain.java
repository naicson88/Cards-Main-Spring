package com.naicson.yugioh.data.chainResponsability.user;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.naicson.yugioh.data.dto.AccountManageDTO;
import com.naicson.yugioh.entity.auth.User;

public class UserValidationChain {
	
	private List<HandleUserValidation> handlers;
	
	//Reference: https://java-design-patterns.com/patterns/chain-of-responsibility/#explanation
	public UserValidationChain(PasswordEncoder encoder, User user, AccountManageDTO dto ) {
		ValidateUser valid = new ValidateUser(encoder);
		handlers = Arrays.asList(valid, valid. new ValidateUserPassword(), valid. new ValidateUserEmail());	
		handlers.stream().forEach(handle -> handle.validateUserAttribute(user, dto));
	}
}
