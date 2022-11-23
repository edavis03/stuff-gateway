package com.example.stuffgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;

@SpringBootApplication
public class StuffGatewayApplication {

    @Bean
    public SecurityWebFilterChain pkceFilterChain(ServerHttpSecurity http, ServerOAuth2AuthorizationRequestResolver resolver) {        
        http.authorizeExchange(r -> r.anyExchange().authenticated());
        http.oauth2Login(auth -> auth.authorizationRequestResolver(resolver));
        return http.build();
    }

    @Bean
    public ServerOAuth2AuthorizationRequestResolver pkceResolver(ReactiveClientRegistrationRepository repo) {
        DefaultServerOAuth2AuthorizationRequestResolver resolver = new DefaultServerOAuth2AuthorizationRequestResolver(repo);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }

	public static void main(String[] args) {
		SpringApplication.run(StuffGatewayApplication.class, args);
	}
}
