package com.shootr.android.service;

import java.io.IOException;

public class ShootrError {

    public String errorCode;
    public String message;
    public String explanation;

    public ShootrServerException asException() {
        return new ShootrServerException(this);
    }
}
