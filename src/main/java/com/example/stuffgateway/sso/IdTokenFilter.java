package com.example.stuffgateway.sso;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class IdTokenFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain){
        return exchange.getPrincipal()
        .filter(principal -> principal instanceof OAuth2AuthenticationToken)
        .cast(OAuth2AuthenticationToken.class)
        .flatMap(token -> {
            OidcUser oidcUser = (OidcUser) token.getPrincipal();
            ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(h -> h.add("IdToken", oidcUser.getIdToken().getTokenValue())).build();
            exchange.getResponse().getHeaders().add("IdToken", oidcUser.getIdToken().getTokenValue());
            
            return chain.filter(exchange.mutate().request(request).build());
        });
    }

}
