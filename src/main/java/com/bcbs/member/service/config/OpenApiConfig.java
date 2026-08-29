package com.bcbs.member.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI memberPlatformOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("BCBS Member Platform API")
                        .version("v1")
                        .description(
                                "REST API for managing BCBS member records, " +
                                        "including creation, retrievel, updates, deletion, " +
                                        "pagination, and status filtering."
                        )
                        .contact(new Contact()
                                .name("BCBS Member Platform Team"))
                        .license(new License()
                                .name("Internal POC")));
    }
}