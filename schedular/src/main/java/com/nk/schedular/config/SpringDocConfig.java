package com.nk.schedular.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SpringDocConfig {
    @Value("${server.servlet.context-path}") private String host;
    @Bean
    public OpenAPI api() {
        Server server = new Server();
        server.setUrl(host);
        return new OpenAPI()
                .servers(List.of(server))
                // .components(new Components().addSecuritySchemes("bearer-key", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info().title("Schedular::Task Management Service API")
                        .version("v1"))
                .externalDocs(new ExternalDocumentation());
                        // .description("user-management-service GitLab")
                        // .url("https://gitlab.eng.vmware.com/cio/bookings/user-management-service"));
    }
}