package edu.itba.class1.exchange.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.Test;

class UnirestHttpClientTest {

    private final UnirestHttpClient client = new UnirestHttpClient();

    @Test
    void reportsAServerErrorWhenTheHostIsUnreachable() {
        var response = client.get(URI.create("http://localhost:1"), Map.of(), Map.of());

        assertThat(response.statusCode()).isEqualTo(500);
        assertThat(response.body()).contains("Internal Server Error");
    }
}
