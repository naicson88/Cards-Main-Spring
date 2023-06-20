package com.naicson.yugioh.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
//@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
	public OpenAPI openAPI() {
	    return new OpenAPI()
            .info(new Info()
                    .title("Cards API")
                    .description("Cards API base for YugiHub")
                    .version("1.0")
                    .termsOfService("Termo de uso: Limited")
                    .license(new License()
                            .name("Apache 2.0")
                            .url("http://www.yugihub.com")
                    )
            ).externalDocs(
                    new ExternalDocumentation()
                    .description("Alan Naicson")
                    .url("http://www.yugihub.com"));
	}
	
	@Bean
	 public OpenAPI customOpenAPI() {
	   return new OpenAPI().components(new Components().addSecuritySchemes("bearer-key",
	     new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
	}
	
//	@Bean
//	public Docket api() {
//		return new Docket(DocumentationType.SWAGGER_2)
//				.select()
//				.apis(RequestHandlerSelectors.any())
//				.paths(PathSelectors.any())
//				.build()				
//				.apiInfo(metaInfo())
//				.securitySchemes(Arrays.asList(apiKey()));
//	}
//
//	private ApiInfo metaInfo() {
//		return new ApiInfoBuilder()
//				.title("Cards API base for YugiHub")
//				.description("")
//				.version("1.0")
//				.termsOfServiceUrl("localhost")
//				.build();
//	}
//	
//	  private ApiKey apiKey() {
//	        return new ApiKey("JWT", "Authorization", "header");
//	    }
}
