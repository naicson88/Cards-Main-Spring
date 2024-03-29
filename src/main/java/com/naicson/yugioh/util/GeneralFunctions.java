package com.naicson.yugioh.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.naicson.yugioh.service.user.UserDetailsImpl;
public abstract class GeneralFunctions {
	
	private GeneralFunctions() {}

	static Logger logger = LoggerFactory.getLogger(GeneralFunctions.class);

	private static Random random = new Random();

	public static UserDetailsImpl userLogged() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();

		if (user == null || user.getId() == 0) {
			logger.error("COULD'T FIND THE LOGGED USER");
			throw new InternalAuthenticationServiceException("Could't find user");
		}

		return user;
	}

//	public static String momentAsString() {
//		String hour = String.valueOf(LocalDateTime.now().getHour());
//		String minutes = String.valueOf(LocalDateTime.now().getMinute());
//		String seconds = String.valueOf(LocalDateTime.now().getSecond());
//
//		String day = String.valueOf(LocalDateTime.now().getDayOfMonth());
//		String month = String.valueOf(LocalDateTime.now().getMonthValue());
//		String year = String.valueOf(LocalDateTime.now().getYear());
//
//		String moment = day + month + year + hour + minutes + seconds;
//
//		return moment;
//	}
	
	public static String randomUniqueValue() {

		String day = String.valueOf(LocalDateTime.now().getDayOfMonth());
		String month = String.valueOf(LocalDateTime.now().getMonthValue());
		String year = String.valueOf(LocalDateTime.now().getYear());

		String moment = day + month + year;
		
		int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 3;
	    
	    Random random = new Random();

	    String generatedString = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();

	    return generatedString+moment;
	}

	public static void saveCardInFolder(Long cardNumber, String url, String folder) {
		try (InputStream in = new URL(url + cardNumber + ".jpg")
				.openStream()) {
			
			Files.copy(in, Paths.get(folder + cardNumber + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
			logger.info("Card saved in folder");

//			try {
//			
//				
//				
//			} catch (FileAlreadyExistsException e) {
//				logger.warn(e.getLocalizedMessage());
//				String randomString = RandomStringUtils.randomAlphabetic(10);
//				Files.copy(in, Paths.get(folder + cardNumber +"-"+randomString+".jpg"));
//			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public static String getRandomDeckCase() {
		String[] arrayBoxCases = { "https://i.imgur.com/6aq3xPy.png", "https://i.imgur.com/3pyZ4U9.png",
				"https://i.imgur.com/cMb2tTF.png", "https://i.imgur.com/ZZvw3oo.png", "https://i.imgur.com/389kGKl.png",
				"https://i.imgur.com/7E3tMCA.png" };

		int randomIndex = random.nextInt(arrayBoxCases.length);
		return arrayBoxCases[randomIndex];
	}

	public static String getRandomCollectionCase() {
		String[] arrayBoxCases = { "https://i.imgur.com/1obzCUB.png", "https://i.imgur.com/Lnxe5WI.png",
				"https://i.imgur.com/s4yGaCe.png", "https://i.imgur.com/bK2lgrq.png" };

		int randomIndex = random.nextInt(arrayBoxCases.length);
		return arrayBoxCases[randomIndex];
	}

	public static int parseValueToInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			logger.error("Error when tryng parse value: {}", value);
			return 0;
		}
	}

	public static void createNewFile(String fileName, String fileExtension, String path, List<String> informations) {

		if (fileExtension == null || fileExtension.isBlank() || informations == null || fileName == null
				|| fileName.isBlank())
			throw new IllegalArgumentException("Invalid file extension or file name informed");
		
			path = path == null || path.isBlank() ? "C:\\Cards\\" : path;
			String fullPath = path + fileName + fileExtension;
			File file = new File(fullPath);

		try (
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			){
			
			logger.info("Starting creating a new File...");

			if (!file.exists())
				file.createNewFile();

			informations.stream().forEach(n -> {
				try {
					bw.write(n);
					bw.newLine();
				} catch (IOException e) {
					logger.error("Error while writing information: {}", n);
				}
			});

			logger.info("File created successfully! {}", fullPath);
		} catch (Exception e) {
			logger.error("Error while generaton File: {}", e.getMessage());

		}
	}
	
	

}
