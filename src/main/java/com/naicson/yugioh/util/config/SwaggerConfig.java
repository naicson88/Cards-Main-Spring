package com.naicson.yugioh.util.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build()				
				.apiInfo(metaInfo())
				.securitySchemes(Arrays.asList(apiKey()));
	}

	private ApiInfo metaInfo() {
		return new ApiInfoBuilder()
				.title("Yu Gi Oh API base for YugiHub")
				.description("")
				.version("1.0")
				.termsOfServiceUrl("localhost")
				.build();
	}
	
	  private ApiKey apiKey() {
	        return new ApiKey("JWT", "Authorization", "header");
	    }
}
