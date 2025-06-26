package com.example.GestionClinique.configuration.swaggerConfig;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SwaggerApi { // Renamed from SwaggerConfig for clarity

    @Bean
    @Primary //
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("API avec JWT")
                        .version("1.0"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
    }

    @Bean
    public OpenAPI gestionCliniqueOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Gestion Clinique Rest API") // Your title
                        .description("Documentation Api de Gestion d'une clinique") // Your description
                        .version("v0.0.1") // Your application version
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Gestion Clinique Wiki Documentation")
                        .url("https://springdoc.org/")); // Or your actual wiki URL
    }


    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("Gestion Clinique API") // The name of your group
                .pathsToMatch("/**") // Document all paths. Adjust if you have a specific base path, e.g., "/api/**"
                .packagesToScan("com.example.GestionClinique") // Scan your main package
                .build();
    }



}
