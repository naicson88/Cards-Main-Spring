package com.naicson.yugioh.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.data.security.JwtUtils;
import com.naicson.yugioh.entity.auth.ERole;
import com.naicson.yugioh.entity.auth.JwtResponse;
import com.naicson.yugioh.entity.auth.LoginRequest;
import com.naicson.yugioh.entity.auth.Role;
import com.naicson.yugioh.entity.auth.SignupRequest;
import com.naicson.yugioh.entity.auth.User;
import com.naicson.yugioh.repository.RoleRepository;
import com.naicson.yugioh.repository.UserRepository;
import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.exceptions.ErrorMessage;
import com.naicson.yugioh.util.exceptions.MessageResponse;
import com.naicson.yugioh.util.mail.EmailService;

@Service
public class AuthServiceImpl {
	
	private static String regexEmailPattern = "^(.+)@(.+)$";
	
	Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
	
	@Autowired
	AuthenticationManager authManeger;
	@Autowired
	UserRepository userRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	EmailService emailService;
	
	public ResponseEntity<?> authenticateUser(LoginRequest loginRequest){
		
		Authentication auth = authManeger.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwt = jwtUtils.generateJwtToken(auth);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		
		return ResponseEntity.ok(new JwtResponse(jwt, 
				 userDetails.getId(),
				 userDetails.getUsername(), 
				 userDetails.getEmail(), 
				 roles,
				 userDetails.getIsEmailConfirmed(),
				 userDetails.getMaxDateValidation(),
				 userDetails.getVerificationToken()));		
	}
	
	public ResponseEntity<?> registerUser(SignupRequest signUpRequest){
		
		if(this.validateRegisterData(signUpRequest) != null)
			return validateRegisterData(signUpRequest);
		
		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRole();
		//Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByRoleName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			user.setRole(userRole);
			//roles.add(userRole);	
		} 
//			else {
//			strRoles.forEach(role -> {				
//				switch (role) {
//				case "admin":
//					Role adminRole = roleRepository.findByRoleName(ERole.ROLE_ADMIN)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(adminRole);
//					break;
//					
//				case "mod":
//					Role modRole = roleRepository.findByRoleName(ERole.ROLE_MODERATOR)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(modRole);
//
//					break;
//				default:
//					Role userRole = roleRepository.findByRoleName(ERole.ROLE_USER)
//							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//					roles.add(userRole);
//				}
//			});
//			
//		}
		
		//user.setRoles(roles);
		userRepository.save(user);
		logger.info("New User registered! {}", LocalDateTime.now());
		emailService.sendEmail(user);
		
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}
	
	private ResponseEntity<?> validateRegisterData(SignupRequest signUpRequest) {
		
		if(signUpRequest.getUsername() == null || signUpRequest.getUsername().length() <= 3) {
			logger.error("Error: User Name too short! {}", signUpRequest.getUsername());
			return ResponseEntity
					.badRequest()
					.body(new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "Error: User Name too short!"));
		}
		
		if (userRepository.existsByUserName(signUpRequest.getUsername())) {
			logger.error("Error: Username is already taken! {}", signUpRequest.getUsername());
			return ResponseEntity
					.badRequest()
					.body(new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "Error: Username is already taken!"));
		}
		
		if (signUpRequest.getEmail() == null || signUpRequest.getEmail().isBlank()) {
			logger.error("Error: Invalid Email informed! {}", signUpRequest.getEmail());
			return ResponseEntity
					.badRequest()
					.body(new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "Error: Invalid Email informed!"));
		} else {
			Pattern pattern = Pattern.compile(regexEmailPattern);
			Matcher matcher = pattern.matcher(signUpRequest.getEmail());
			
			if(!matcher.matches()) {
				logger.error("Error: Invalid Email pattern! {}", signUpRequest.getEmail());
				return ResponseEntity
						.badRequest()
						.body(new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "Error: Invalid Email informed!"));
			}
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			logger.error("Error: Email is already in use! {}", signUpRequest.getEmail());
			return ResponseEntity
					.badRequest()
					.body(new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "Error: Email is already in use!"));
		}
		
		return null;
	}

	public ResponseEntity<?> tokenValidation(String token) {
		
		User user = userRepository.findByVerificationToken(token)
				.orElseThrow(()-> new EntityNotFoundException("It was not possible find User with this token"));
		
		if(user.getIsEmailConfirmed()) {
			logger.info("User was validated successfully: {} ", user.getUserName());
			return ResponseEntity
					.ok()
					.body("Success: User was validated successfully!");
		}
		
		else if(user.getMaxDateValidation().isBefore(LocalDateTime.now())) {			
			userRepository.delete(user);			
			return ResponseEntity
					.badRequest()
					.body(new ErrorMessage(HttpStatus.NOT_ACCEPTABLE, "Error: The max date for validation is expired!"));
		} else {
			user.setIsEmailConfirmed(true);
			userRepository.save(user);
			logger.info("User was validated successfully: {} ", user.getUserName());
			return ResponseEntity
					.ok()
					.body("Success: User was validated successfully!");
		}	
	}	
}
