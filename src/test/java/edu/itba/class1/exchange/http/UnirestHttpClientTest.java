package edu.itba.class1.exchange.http;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;

import java.net.URI;
import java.util.Map;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnirestHttpClientTest {

    private final UnirestHttpClient client = new UnirestHttpClient();
    private WireMockServer server;

    @BeforeEach
    void startServer() {
        server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        server.start();
        configureFor("localhost", server.port());
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void returnsTheSuccessfulHttpResponse() {
        server.stubFor(get(urlPathEqualTo("/latest"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"data\":{\"EUR\":0.92}}")));

        var response = client.get(
                URI.create(server.baseUrl() + "/latest"),
                Map.of("base_currency", "USD"),
                Map.of("apikey", "test-key"));

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isEqualTo("{\"data\":{\"EUR\":0.92}}");
    }

    @Test
    void preservesTransportFailureWhenTheHostIsUnreachable() {
        assertThatThrownBy(() -> client.get(URI.create("http://localhost:1"), Map.of(), Map.of()))
                .isInstanceOf(HttpTransportException.class)
                .hasCauseInstanceOf(Exception.class);
    }
}
