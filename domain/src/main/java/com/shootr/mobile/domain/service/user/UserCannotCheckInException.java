package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.ShootrException;

public class UserCannotCheckInException extends ShootrException {

  public UserCannotCheckInException(Throwable cause) {
    super(cause);
  }
}
