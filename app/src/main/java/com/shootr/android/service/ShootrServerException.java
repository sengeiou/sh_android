package com.shootr.android.service;

import java.io.IOException;

public class ShootrServerException  extends IOException{

    private ShootrError shootrError;

    public ShootrServerException(ShootrError shootrError) {
        this.shootrError = shootrError;
    }

    @Override public String getMessage() {
        return shootrError.errorCode + ": " + shootrError.message;
    }

}
