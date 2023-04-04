package com.naicson.yugioh.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.naicson.yugioh.service.user.UserDetailsImpl;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

public abstract class GeneralFunctions {

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

	public static void saveCardInFolder(Long cardNumber, String url, String folder) {
		try (InputStream in = new URL(url + cardNumber + ".jpg")
				.openStream()) {

			try {
			
				Files.copy(in, Paths.get(folder + cardNumber + ".jpg"));
				logger.info("Card saved in folder");
				
			} catch (FileAlreadyExistsException e) {
				logger.warn(e.getLocalizedMessage());
				String randomString = RandomStringUtils.randomAlphabetic(10);
				Files.copy(in, Paths.get(folder + cardNumber +"-"+randomString+".jpg"));
			}

		} catch (IOException e) {
			e.getMessage();
			throw new ErrorMessage(e.getMessage());
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

		try {
			logger.info("Starting creating a new File...");

			path = path == null || path.isBlank() ? "C:\\Cards\\" : path;
			String fullPath = path + fileName + fileExtension;
			File file = new File(fullPath);

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			fw.close();

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
			bw.close();
			logger.info("File created successfully! {}", fullPath);
		} catch (Exception e) {
			logger.error("Error while generaton File: {}", e.getMessage());

		} finally {

		}
	}

}
