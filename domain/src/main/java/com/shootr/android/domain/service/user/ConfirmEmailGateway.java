package com.shootr.android.domain.service.user;

import com.shootr.android.domain.exception.EmailAlreadyConfirmedException;
import com.shootr.android.domain.exception.EmailAlreadyExistsException;
import com.shootr.android.domain.exception.InvalidEmailConfirmationException;
import com.shootr.android.domain.exception.UnauthorizedRequestException;

public interface ConfirmEmailGateway {

    void confirmEmail() throws InvalidEmailConfirmationException;

    void changeEmail(String email)
      throws EmailAlreadyExistsException, EmailAlreadyConfirmedException,
      UnauthorizedRequestException;
}
