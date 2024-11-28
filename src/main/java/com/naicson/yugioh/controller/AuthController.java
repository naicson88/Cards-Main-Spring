package com.naicson.yugioh.controller;

import com.naicson.yugioh.data.builders.ResponseBuilderAPI;
import com.naicson.yugioh.data.dto.AccountManageDTO;
import com.naicson.yugioh.entity.auth.LoginRequest;
import com.naicson.yugioh.entity.auth.SignupRequest;
import com.naicson.yugioh.entity.auth.User;
import com.naicson.yugioh.service.AuthServiceImpl;
import com.naicson.yugioh.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping({"yugiohAPI/auth"}) 
public class AuthController {
	
	@Autowired
	AuthServiceImpl authService;
	
	Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Operation(summary="Login in the application")
	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		return authService.authenticateUser(loginRequest);
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?>  registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		return authService.registerUser(signUpRequest);
	}
	
	@Operation(summary="Validate a token to condirm Email")
	@GetMapping("/token-validation")
	public ResponseEntity<?> tokenValidation(@RequestParam("token") String token) {
		return authService.tokenValidation(token);
	}
	
	@Operation(summary= "Send a email to reset password")
	@GetMapping("/resend-password")
	public ResponseEntity<?> resendPassword(@RequestParam("email") String email){
		return authService.resendPassword(email);
	}
	
	@Operation(summary= "Check token for change password")
	@GetMapping("/check-token-password")
	public ResponseEntity<?> checkTokenToChangePassword(@RequestParam("token") String token){
		return authService.checkTokenToChangePassword(token);
	}
	
	@Operation(summary= "Change User password")
	@PostMapping("/change-password")
	public ResponseEntity<?> changeUserPassword(@RequestBody User user){
		return authService.changeUserPassword(user);
	}
	
	@Operation(summary= "Change User Account Information")
	@PostMapping("/change-account-information")
	public ResponseEntity<ApiResponse<Object>> changeAccountInformation(@RequestBody AccountManageDTO dto){	
		
		authService.changeAccountInformation(dto);
		
		ResponseBuilderAPI response = new ResponseBuilderAPI();
		return response
				.buildFullResponse(null , HttpStatus.OK.value(), "Account information changed successfully", dto, null);			
	} 
	
	@Operation(summary= "Confirm User's password")
	@GetMapping("/confirm-password")
	public ResponseEntity<String> isPasswordCorrect(@RequestParam("pass") String password){	
		if(	authService.isPasswordCorrect(password) )
			return new ResponseEntity<>(JSONObject.quote("Correct!"), HttpStatus.OK);
		else
			return new ResponseEntity<>("Wrong!", HttpStatus.FORBIDDEN);
	}

	@Cacheable("isAdmin")
	@GetMapping("/is-admin/{userName}")
	public ResponseEntity<Map<String, Boolean>> isUserAdmin(@PathVariable("userName") String userName) {
		Boolean isAdmin = authService.isUserAdmin(userName);
		Map<String, Boolean> map = Map.of("isAdmin", isAdmin);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}
}
