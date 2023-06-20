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
		if(dto.getUsername() != null && !dto.getUsername().isBlank()) {
			if(dto.getUsername().length() < 6)
				throw new IllegalArgumentException("Invalid Username");
			
			user.setUserName(dto.getUsername());  
		 }
		
		return user;
	}
	
	public class ValidateUserPassword implements HandleUserValidation {

		@Override
		public User validateUserAttribute(User user, AccountManageDTO dto) {	
			if(dto.getPass() != null && !dto.getPass().isBlank()) {
				
				if(!dto.getPass().equals(dto.getNewpass()))
					throw new IllegalArgumentException("Passwords value dont match!");
				if(dto.getPass().length() < 6)
					throw new IllegalArgumentException("Password value is too small");
				
				user.setPassword(encoder.encode(dto.getPass()));
			}
			
			return user;
		}

	}
	
	public class ValidateUserEmail implements HandleUserValidation{

		@Override
		public User validateUserAttribute(User user, AccountManageDTO dto) {			
			if(dto.getEmail() != null && !dto.getEmail().isBlank()) {				
				if(!dto.getEmail().equals(dto.getNewemail()))
					throw new IllegalArgumentException("Emails value dont match!");
				if(dto.getEmail().length() < 6)
					throw new IllegalArgumentException("Email value is too small");
				
				user.setEmail(dto.getEmail());
			}
			
			return user;
		}
		
	}
}


