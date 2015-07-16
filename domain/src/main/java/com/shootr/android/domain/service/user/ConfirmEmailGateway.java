package com.shootr.android.domain.service.user;

import com.shootr.android.domain.exception.InvalidEmailConfirmationException;

public interface ConfirmEmailGateway {

    void confirmEmail() throws InvalidEmailConfirmationException;

    void changeEmail(String email);
}
