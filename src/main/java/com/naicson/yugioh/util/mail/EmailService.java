package com.naicson.yugioh.util.mail;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.naicson.yugioh.entity.auth.User;
import com.naicson.yugioh.util.exceptions.ErrorMessage;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private EmailContentBuilder builder;
	
	@Value("${angular.path}")
	private String angularUrl;
	
	Logger logger = LoggerFactory.getLogger(EmailService.class);
	
	public void sendEmail(User user) {
		
		//String url = "http://localhost:8080/yugiohAPI/auth/token-validation?token="+user.getVerificationToken();
		String url = angularUrl+"/confirmation?token="+user.getVerificationToken();
		String userName = user.getUserName();
		
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
			helper.setFrom("no-reply-yugihub@yugihub.com");
			helper.setTo(user.getEmail());
			helper.setSubject("Confirm your registration on YugiHub!");
			helper.setText(builder.build(url, userName), true);			
		};
		
		try {
			mailSender.send(messagePreparator);
			logger.info("Email sent for confirmation! {}", LocalDateTime.now());
		}catch(MailException e) {
			throw new ErrorMessage("Something bad happened when tried to send email to: " + user.getEmail() + ". " + e.getMessage());
		}
	
	}
	
	public void resendPasswordEmail(User user) {
		String url = angularUrl+"/register?change-password=true&token="+user.getVerificationToken();
		String userName = user.getUserName();
		
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
			helper.setFrom("no-reply-yugihub@yugihub.com");
			helper.setTo(user.getEmail());
			helper.setSubject("Change your password!");
			helper.setText(builder.buildResendPassword(url, userName), true);			
		};
		
		try {
			mailSender.send(messagePreparator);
			logger.info("Email sent for change password! {}", LocalDateTime.now());
		}catch(MailException e) {
			throw new ErrorMessage("Something bad happened when tried to send email to: " + user.getEmail() + ". " + e.getMessage());
		}
	}
	
}
