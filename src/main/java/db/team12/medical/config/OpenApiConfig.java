package db.team12.medical.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =
                @Info(
                        title = "Medical System API",
                        version = "v1",
                        description = "의료 정보 및 처방 관리를 위한 내부 API 문서",
                        contact = @Contact(name = "Team12", email = "team12@example.com")),
        servers = {
            @Server(url = "http://localhost:8080", description = "로컬 개발 서버")
        },
        security = {@SecurityRequirement(name = "bearerAuth")})
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

    @Bean
    public OpenAPI medicalOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(
                                "bearerAuth",
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
                                        .name("Authorization")))
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearerAuth"));
    }
}
