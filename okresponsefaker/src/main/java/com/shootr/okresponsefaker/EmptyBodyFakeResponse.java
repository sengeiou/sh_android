package com.shootr.okresponsefaker;

public abstract class EmptyBodyFakeResponse implements FakeResponse {

    @Override
    public String body() {
        return "";
    }

    @Override
    public String mediaType() {
        return "text/plain";
    }
}
