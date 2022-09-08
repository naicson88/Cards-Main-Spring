package com.naicson.yugioh.util.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailContentBuilder {
	
	@Autowired
	private TemplateEngine template;
	
	public String build(String url, String userName) {
		Context context = new Context();
		context.setVariable("url", url);
		context.setVariable("userName", userName);
		String process = template.process("EmailTemplate", context);
		
		return process;
		 
	}
	
	public String buildResendPassword(String url, String userName) {
		Context context = new Context();
		context.setVariable("url", url);
		context.setVariable("userName", userName);
		String process = template.process("EmailTemplateResend", context);
		
		return process;
		
	}
}
