package Tugas2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {

    private final HttpExchange httpExchange;
    private final Headers headers;
    private String rawBody;

    public Request(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
        this.headers = httpExchange.getRequestHeaders();
    }

    public String getBody() {
        if (this.rawBody == null) {
            this.rawBody = new BufferedReader(
                    new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)
            ).lines().collect(Collectors.joining("\n"));
        }
        return this.rawBody;
    }

    public String getRequestMethod() {
        return httpExchange.getRequestMethod();
    }

    public String getContentType() {
        String contentType = headers.getFirst("Content-Type");
        return contentType != null ? contentType.trim() : "";
    }

    public Map<String, Object> getJSON() throws JsonProcessingException {
        String contentType = getContentType();
        if (!contentType.equalsIgnoreCase("application/json")) {
            throw new JsonProcessingException("Invalid Content-Type: " + contentType) {};
        }

        String body = getBody();
        if (body == null || body.isEmpty()) {
            throw new JsonProcessingException("Request body is empty") {};
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
    }

    public String getHeader(String key) {
        return headers.getFirst(key);
    }
}
