package com.naicson.yugioh.entity.auth;

import java.time.LocalDateTime;
import java.util.List;

public class JwtResponse {
	
	private String token;
	private String type = "Bearer";
	private long id;
	private String username;
	private String email;
	private List<String> roles;
	private boolean isEmailConfirmed;
	private LocalDateTime maxDateValidation;
	private String verificationToken;  


	public JwtResponse(String accessToken, long id, String username, String email,
			List<String> roles, boolean isEmailConfirmed, 
			LocalDateTime maxDateValidation, String verificationToken ) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.isEmailConfirmed = isEmailConfirmed;
		this.maxDateValidation = maxDateValidation;
		this.verificationToken = verificationToken;
	}

	public String getAccessToken() {
		return token;
	}

	public void setAccessToken(String accessToken) {
		this.token = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<String> getRoles() {
		return roles;
	}
	

	public boolean isEmailConfirmed() {
		return isEmailConfirmed;
	}

	public void setEmailConfirmed(boolean isEmailConfirmed) {
		this.isEmailConfirmed = isEmailConfirmed;
	}

	public LocalDateTime getMaxDateValidation() {
		return maxDateValidation;
	}

	public void setMaxDateValidation(LocalDateTime maxDateValidation) {
		this.maxDateValidation = maxDateValidation;
	}

	public String getVerificationToken() {
		return verificationToken;
	}

	public void setVerificationToken(String verificationToken) {
		this.verificationToken = verificationToken;
	}
}
