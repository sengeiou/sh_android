package com.shootr.okresponsefaker;

public interface FakeResponse {

    String body();

    String mediaType();

    int httpCode();
}
