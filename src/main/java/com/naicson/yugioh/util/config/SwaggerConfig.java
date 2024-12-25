package com.naicson.yugioh.util.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		final String securitySchemeName = "bearer-key";
		return new OpenAPI()
				.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
				.components(
						new Components()
								.addSecuritySchemes(securitySchemeName,
										new SecurityScheme()
												.name(securitySchemeName)
												.type(SecurityScheme.Type.HTTP)
												.scheme("bearer")
												.bearerFormat("JWT")
								)
				)
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

}
