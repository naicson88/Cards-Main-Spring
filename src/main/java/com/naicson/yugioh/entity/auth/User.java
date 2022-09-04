package com.naicson.yugioh.entity.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "users", uniqueConstraints = {
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email")
})
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String userName;
	private String email;
	private String password;  
    private Boolean isEmailConfirmed;  
    private String verificationToken; 
    private LocalDateTime maxDateValidation;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
	
//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(	name = "user_roles", 
//				joinColumns = @JoinColumn(name = "user_id"), 
//				inverseJoinColumns = @JoinColumn(name = "role_id"))
//	private Set<Role> roles  = new HashSet<>();
	
	public User() {
		
	}
	
	public User(String username, String email, String password) {
		this.userName = username;
		this.email = email;
		this.password = password;
		this.isEmailConfirmed = false;
		this.verificationToken = UUID.randomUUID().toString();
		this.maxDateValidation = LocalDateTime.now().plusDays(1);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Boolean getIsEmailConfirmed() {
		return isEmailConfirmed;
	}

	public void setIsEmailConfirmed(Boolean isEmailConfirmed) {
		this.isEmailConfirmed = isEmailConfirmed;
	}

	public String getVerificationToken() {
		return verificationToken;
	}

	public void setVerificationToken(String verificationToken) {
		this.verificationToken = verificationToken;
	}

	public LocalDateTime getMaxDateValidation() {
		return maxDateValidation;
	}

	public void setMaxDateValidation(LocalDateTime maxDateValidation) {
		this.maxDateValidation = maxDateValidation;
	}
		
}
