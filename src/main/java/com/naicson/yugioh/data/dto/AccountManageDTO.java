package com.naicson.yugioh.data.dto;

import com.naicson.yugioh.entity.auth.User;

public class AccountManageDTO {
	
	private String username;
	private String pass;
	private String newpass;
	private String email;
	private String newemail;
	private User user;
	
	public AccountManageDTO() {}
	
	public AccountManageDTO(String username, String pass, String newpass, String email, String newemail) {
		super();
		this.username = username;
		this.pass = pass;
		this.newpass = newpass;
		this.email = email;
		this.newemail = newemail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getNewpass() {
		return newpass;
	}

	public void setNewpass(String newpass) {
		this.newpass = newpass;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNewemail() {
		return newemail;
	}

	public void setNewemail(String newemail) {
		this.newemail = newemail;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
		
}
