package com.shootr.android.service;

import com.shootr.android.exception.ShootrError;
import java.io.IOException;

public class ShootrServerException  extends IOException{

    private ShootrError shootrError;

    public ShootrServerException(ShootrError shootrError) {
        this.shootrError = shootrError;
    }

    public ShootrError getShootrError() {
        return shootrError;
    }

    @Override public String getMessage() {
        return shootrError.getErrorCode() + ": " + shootrError.getMessage();
    }

}
