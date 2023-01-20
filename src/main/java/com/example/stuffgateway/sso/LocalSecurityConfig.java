package com.example.stuffgateway.sso;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@Profile("local")
public class LocalSecurityConfig {
    
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http){
        http.csrf().disable()
            .authorizeExchange().anyExchange().permitAll();
        return http.build();
    }
}
