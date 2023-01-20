package com.example.stuffgateway.sso;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.server.ServerWebExchange;

import static org.assertj.core.api.Assertions.assertThat;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class IdTokenFilterTest {
    public IdTokenFilter idTokenFilter;
    public GatewayFilterChain gatewayFilterChain;
    public ServerWebExchange exchange;
    public ArgumentCaptor<ServerWebExchange> exchangeCaptor;
    public OAuth2AuthenticationToken token;

    @BeforeEach
    void beforeEach() {
        idTokenFilter = new IdTokenFilter();

        exchangeCaptor = ArgumentCaptor.forClass(ServerWebExchange.class);
        gatewayFilterChain = mock(GatewayFilterChain.class);
        when(gatewayFilterChain.filter(exchangeCaptor.capture())).thenReturn(Mono.empty());

        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        token = createPrincipal();
        exchange = MockServerWebExchange.from(request).mutate().principal(Mono.just(token)).build();
    }

    @Test
    void filter_shouldAddOAuthIdTokenRequestHeader() {
        StepVerifier.create(idTokenFilter.filter(exchange, gatewayFilterChain)).expectComplete().verify();

        ServerHttpRequest actualRequest = exchangeCaptor.getValue().getRequest();
        assertThat(actualRequest.getHeaders()).containsKey("IdToken");
        OidcUser oidcUser = (OidcUser) token.getPrincipal();
        assertThat(actualRequest.getHeaders().get("IdToken").get(0)).isEqualTo(oidcUser.getIdToken().getTokenValue());
    }

    @Test
    void filter_shouldAddOAuth2IdTokenResponseHeader() {
        StepVerifier.create(idTokenFilter.filter(exchange, gatewayFilterChain)).expectComplete().verify();

        ServerHttpResponse actualResponse = exchangeCaptor.getValue().getResponse();
        assertThat(actualResponse.getHeaders()).containsKey("IdToken");
        OidcUser oidcUser = (OidcUser) token.getPrincipal();
        assertThat(actualResponse.getHeaders().get("IdToken").get(0)).isEqualTo(oidcUser.getIdToken().getTokenValue());
    }

    private OAuth2AuthenticationToken createPrincipal() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        DefaultOidcUser oidc2User = new DefaultOidcUser(authorities, new OidcIdToken("lindsey", Instant.now(), Instant.now(), Map.of("sub", "abcd")));
        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(oidc2User, authorities, "okta");
        return token;
    }
}