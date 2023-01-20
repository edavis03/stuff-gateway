package com.example.stuffgateway.sso;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureWireMock
@ActiveProfiles("cloud")
public class SsoConfigurationTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    public void filterChain_shouldRedirectAllUrisToAuthenticate() {
        webClient.get().uri("/").exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueEquals("location", "/oauth2/authorization/okta");

        webClient.get().uri("/api").exchange()
            .expectStatus().is3xxRedirection();
    }

    @Test
    public void filterChain_shouldRedirectAllUrisToAuthenticateWithPkce() {
        FluxExchangeResult<String> redirectToOktaResult = webClient.get().uri("/oauth2/authorization/okta").exchange()
            .expectStatus().is3xxRedirection()
            .returnResult(String.class);

        String locationHeader = redirectToOktaResult.getResponseHeaders().get("location").get(0);
        assertThat(locationHeader).contains("https://dev-42225833.okta.com/oauth2/v1/authorize?response_type=code&client_id=");
        assertThat(locationHeader).contains("code_challenge");
    }
}
