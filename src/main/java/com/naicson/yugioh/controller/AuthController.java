package com.naicson.yugioh.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.naicson.yugioh.data.builders.ResponseBuilder;
import com.naicson.yugioh.data.dto.AccountManageDTO;
import com.naicson.yugioh.entity.auth.LoginRequest;
import com.naicson.yugioh.entity.auth.SignupRequest;
import com.naicson.yugioh.entity.auth.User;
import com.naicson.yugioh.service.AuthServiceImpl;
import com.naicson.yugioh.util.ApiResponse;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping({"yugiohAPI/auth"}) 
public class AuthController {
	
	@Autowired
	AuthServiceImpl authService;
	
	Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@ApiOperation(value="Login in the application")
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		return authService.authenticateUser(loginRequest);		
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		return authService.registerUser(signUpRequest);
	}
	
	@ApiOperation(value="Validate a token to condirm Email")
	@GetMapping("/token-validation")
	public ResponseEntity<?> tokenValidation(@RequestParam("token") String token) {
		return authService.tokenValidation(token);
	}
	
	@ApiOperation(value = "Send a email to reset password")
	@GetMapping("/resend-password")
	public ResponseEntity<?> resendPassword(@RequestParam("email") String email){
		return authService.resendPassword(email);
	}
	
	@ApiOperation(value = "Check token for change password")
	@GetMapping("/check-token-password")
	public ResponseEntity<?> checkTokenToChangePassword(@RequestParam("token") String token){
		return authService.checkTokenToChangePassword(token);
	}
	
	@ApiOperation(value = "Change User password")
	@PostMapping("/change-password")
	public ResponseEntity<?> changeUserPassword(@RequestBody User user){
		return authService.changeUserPassword(user);
	}
	
	@ApiOperation(value = "Change User Account Information")
	@PostMapping("/change-account-information")
	public ResponseEntity<ApiResponse<Object>> changeAccountInformation(@RequestBody AccountManageDTO dto){	
		
		authService.changeAccountInformation(dto);
		ResponseBuilder response = new ResponseBuilder();
		return response.buildFullResponse(null , HttpStatus.OK.value(), "Account information changed successfully", dto, null);			
	} 

}
