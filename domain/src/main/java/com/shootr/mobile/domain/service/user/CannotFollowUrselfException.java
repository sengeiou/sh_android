package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.ShootrException;

public class CannotFollowUrselfException extends ShootrException {

  public CannotFollowUrselfException(String message) {
    super(message);
  }

  public CannotFollowUrselfException(Throwable cause) {
    super(cause);
  }
}
