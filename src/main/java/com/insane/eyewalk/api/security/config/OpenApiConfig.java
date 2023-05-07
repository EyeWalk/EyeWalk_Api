package com.insane.eyewalk.api.security.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "EyeWalk - Insane Technology",
                        url = "https://github.com/EyeWalk"
                ),
                description = "Insane Technology API documentation",
                title = "EyeWalk API",
                version = "1.0",
                license = @License(
                        name = "License",
                        url = "https://github.com/EyeWalk"
                )
        ),
        servers = {
                @Server(
                        description = "REMOTE ENVIRONMENT",
                        url = "https://api.eyewalk.42g.com.br"
                ),
                @Server(
                        description = "LOCAL ENVIRONMENT",
                        url = "http://localhost:8080"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
