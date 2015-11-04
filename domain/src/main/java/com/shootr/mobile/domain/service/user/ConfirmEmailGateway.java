package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.EmailAlreadyConfirmedException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.InvalidEmailConfirmationException;
import com.shootr.mobile.domain.exception.UnauthorizedRequestException;

public interface ConfirmEmailGateway {

    void confirmEmail() throws InvalidEmailConfirmationException;

    void changeEmail(String email)
      throws EmailAlreadyExistsException, EmailAlreadyConfirmedException,
      UnauthorizedRequestException;
}
