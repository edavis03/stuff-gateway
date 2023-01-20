package com.example.stuffgateway.sso;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@Profile("cloud")
public class CloudSecurityConfig {
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, ServerOAuth2AuthorizationRequestResolver resolver){
        http.csrf().disable()
            .authorizeExchange(r -> r.anyExchange().authenticated())
            .oauth2Login(auth -> auth.authorizationRequestResolver(resolver));
        return http.build();
    }

    @Bean
    public ServerOAuth2AuthorizationRequestResolver pkceRequestResolver(ReactiveClientRegistrationRepository repo){
        var resolver = new DefaultServerOAuth2AuthorizationRequestResolver(repo);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }
}
