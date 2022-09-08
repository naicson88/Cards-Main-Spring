package com.naicson.yugioh.service.user;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.naicson.yugioh.entity.auth.User;

public class UserDetailsImpl implements UserDetails{
	
	private static final long serialVersionUID = 1L;
	private long id;
	private String username;
	private String email;
	@JsonIgnore
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	private boolean isEmailConfirmed;  
	private LocalDateTime maxDateValidation;
	@JsonIgnore
    private String verificationToken; 
	
	public UserDetailsImpl() {
		
	}

	public UserDetailsImpl(long id, String userName, String email, String password,
			Collection<? extends GrantedAuthority> authorities) {
		super();
		this.id = id;
		this.username = userName;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
	}
	
	public UserDetailsImpl(long id, String userName, String email, String password,
			Collection<? extends GrantedAuthority> authorities, boolean isEmailConfirmed, 
			LocalDateTime maxDateValidation, String verificationToken) {
		super();
		this.id = id;
		this.username = userName;
		this.email = email;
		this.password = password;
		this.authorities = authorities;
		this.isEmailConfirmed = isEmailConfirmed;
		this.maxDateValidation = maxDateValidation;
		this.verificationToken = verificationToken;
	}
	
	public static UserDetailsImpl build(User user) {
//		List<GrantedAuthority> authorities = user.getRole().stream()
//				.map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
//				.collect(Collectors.toList());
		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().getRoleName().name()));
		
		//GrantedAuthority authoritie = new SimpleGrantedAuthority(user.getRole().getRoleName().name());
		
		
		return new UserDetailsImpl(user.getId(), user.getUserName(), user.getEmail(),
				user.getPassword(), authorities, user.getIsEmailConfirmed(),
				user.getMaxDateValidation(), user.getVerificationToken());
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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
	

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return (Collection<? extends GrantedAuthority>) authorities;
	}

	public Boolean getIsEmailConfirmed() {
		return isEmailConfirmed;
	}

	public void setIsEmailConfirmed(Boolean isEmailConfirmed) {
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
