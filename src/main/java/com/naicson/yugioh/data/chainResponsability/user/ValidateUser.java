package com.naicson.yugioh.data.chainResponsability.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.dto.AccountManageDTO;
import com.naicson.yugioh.entity.auth.User;

@Service
public class ValidateUser implements HandleUserValidation {
	
	PasswordEncoder encoder;
	
	public ValidateUser(PasswordEncoder encoder) {
		this.encoder = encoder;
	}

	@Override
	public User validateUserAttribute(User user, AccountManageDTO dto) {	
		if(dto.username() != null && !dto.username().isBlank()) {
			if(dto.username().length() < 6)
				throw new IllegalArgumentException("Invalid Username");
			
			user.setUserName(dto.username());
		 }
		
		return user;
	}
	
	public class ValidateUserPassword implements HandleUserValidation {

		@Override
		public User validateUserAttribute(User user, AccountManageDTO dto) {	
			if(dto.pass() != null && !dto.pass().isBlank()) {
				
				if(!dto.pass().equals(dto.newpass()))
					throw new IllegalArgumentException("Passwords value don't match!");
				if(dto.pass().length() < 6)
					throw new IllegalArgumentException("Password value is too small");
				
				user.setPassword(encoder.encode(dto.pass()));
			}
			
			return user;
		}

	}
	
	public class ValidateUserEmail implements HandleUserValidation{

		@Override
		public User validateUserAttribute(User user, AccountManageDTO dto) {			
			if(dto.email() != null && !dto.email().isBlank()) {
				if(!dto.email().equals(dto.newemail()))
					throw new IllegalArgumentException("Emails value dont match!");
				if(dto.email().length() < 6)
					throw new IllegalArgumentException("Email value is too small");
				
				user.setEmail(dto.email());
			}
			
			return user;
		}
		
	}
}


