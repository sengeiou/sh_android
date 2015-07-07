package com.shootr.okresponsefaker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonFakeResponse implements FakeResponse {

    private final ObjectMapper objectMapper;
    private final Object response;
    private final int httpcode;

    public JacksonFakeResponse(ObjectMapper objectMapper, Object response, int httpcode) {
        this.objectMapper = objectMapper;
        this.response = response;
        this.httpcode = httpcode;
    }

    @Override
    public String body() {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String mediaType() {
        return "application/json";
    }

    @Override
    public int httpCode() {
        return httpcode;
    }
}
