package com.naicson.yugioh.util.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailContentBuilder {
	
	@Autowired
	private TemplateEngine template;
	
	public String build(String url) {
		Context context = new Context();
		context.setVariable("url", url);
		String process = template.process("EmailTemplate", context);
		
		return process;
		 
	}
}
