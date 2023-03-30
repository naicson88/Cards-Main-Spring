
package com.naicson.yugioh;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@EnableCaching
public class YugiohApplication {

	public static void main(String[] args) {
		SpringApplication.run(YugiohApplication.class, args);

	}	
	
	
// Open Browser tab when start aplication
	@PostConstruct
	public void execute() throws IOException {
		Runtime rt = Runtime.getRuntime();
		String url = "http://localhost:8080/swagger-ui.html";
		rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
	}

}
