package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.ShootrException;


public class UserAlreadyCheckedException extends ShootrException {

  public UserAlreadyCheckedException(Throwable cause) {
    super(cause);
  }
}
