package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.ForgotPasswordResult;
import com.shootr.mobile.domain.exception.InvalidForgotPasswordException;

import java.io.IOException;

public interface ResetPasswordGateway {

    ForgotPasswordResult performPasswordReset(String usernameOrEmail)
      throws IOException, InvalidForgotPasswordException;
}
