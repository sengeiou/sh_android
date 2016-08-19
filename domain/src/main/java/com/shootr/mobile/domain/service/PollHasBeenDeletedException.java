package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.exception.ShootrException;

public class PollHasBeenDeletedException extends ShootrException {

  public PollHasBeenDeletedException(Throwable cause) {
    super(cause);
  }
}
