package com.naicson.yugioh.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.naicson.yugioh.service.user.UserDetailsImpl;


public abstract class GeneralFunctions {
	
	static Logger logger = LoggerFactory.getLogger(GeneralFunctions.class);
	
	public static UserDetailsImpl userLogged() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
		
		if(user == null || user.getId() == 0) {
			logger.error("COULD'T FIND THE LOGGED USER");
			throw new InternalAuthenticationServiceException("Could't find user");		
		}
						
		return user;
	}
	
	public static String transformArrayInString(int[] array) {
		
		String str = "";

		for (int ints : array) {
			str += ints;
			str += ",";
		}
		str += "0";
		
		return str;
	}
	
	public static String transformArrayInStringForLong(Long[] array) {
		
		String str = "";

		for (Long values : array) {
			str += values;
			str += ",";
		}
		str += "0";
		
		return str;
	}
	
    public static String momentAsString() {
    	String hour = String.valueOf(LocalDateTime.now().getHour());
    	String minutes = String.valueOf(LocalDateTime.now().getMinute());
    	String seconds = String.valueOf(LocalDateTime.now().getSecond());
    	
    	String day = String.valueOf(LocalDateTime.now().getDayOfMonth());
    	String month = String.valueOf(LocalDateTime.now().getMonthValue());
    	String year = String.valueOf(LocalDateTime.now().getYear());
    	
    	String moment = day+month+year+hour+minutes+seconds;
    	
    	return moment;
    }
    
    public static void saveCardInFolder(Long cardNumber) {
    	try(InputStream in = new URL("https://storage.googleapis.com/ygoprodeck.com/pics/"+cardNumber+".jpg").openStream()) {
    		Files.copy(in, Paths.get("C:\\Cards\\"+cardNumber+".jpg"));
    		logger.info("Card saved in folder");
    	}catch(IOException e) {
    		e.getMessage();
    	}
    }
    
    public static String getFolderBySetType(String setType) {
    	
    	if("DECK".equalsIgnoreCase(setType))
			return "deck";
		else if("BOOSTER".equalsIgnoreCase(setType))
			return "booster";
    	if("TIN".equalsIgnoreCase(setType))
			return "tin";
		else if("BOX".equalsIgnoreCase(setType))
			return "box";
    	
    	return null;
    }
    
    public static String getRandomDeckCase() {
     String[] arrayBoxCases = {"https://i.imgur.com/6aq3xPy.png", "https://i.imgur.com/3pyZ4U9.png","https://i.imgur.com/cMb2tTF.png",
    			"https://i.imgur.com/ZZvw3oo.png","https://i.imgur.com/389kGKl.png","https://i.imgur.com/7E3tMCA.png"};
    	Random random = new Random();
    	int randomIndex = random.nextInt(arrayBoxCases.length);
    	return arrayBoxCases[randomIndex];
    }
    
    
    public static String getRandomCollectionCase() {
     String[] arrayBoxCases = {"https://i.imgur.com/1obzCUB.png", "https://i.imgur.com/Lnxe5WI.png","https://i.imgur.com/s4yGaCe.png",
    			"https://i.imgur.com/bK2lgrq.png"};
    	Random random = new Random();
    	int randomIndex = random.nextInt(arrayBoxCases.length);
    	return arrayBoxCases[randomIndex];
    }
}
