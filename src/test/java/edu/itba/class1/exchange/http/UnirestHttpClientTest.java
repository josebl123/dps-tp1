package edu.itba.class1.exchange.http;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.Test;

class UnirestHttpClientTest {

    private final UnirestHttpClient client = new UnirestHttpClient();

    @Test
    void preservesTransportFailureWhenTheHostIsUnreachable() {
        assertThatThrownBy(() -> client.get(URI.create("http://localhost:1"), Map.of(), Map.of()))
                .isInstanceOf(HttpTransportException.class)
                .hasCauseInstanceOf(Exception.class);
    }
}
