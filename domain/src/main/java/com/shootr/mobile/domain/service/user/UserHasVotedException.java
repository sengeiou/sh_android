package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.ShootrException;

public class UserHasVotedException extends ShootrException {

  public UserHasVotedException(Throwable cause) {
    super(cause);
  }
}
