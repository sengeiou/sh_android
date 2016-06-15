package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.ShootrException;

public class StreamHasTooManyPolls extends ShootrException {

  public StreamHasTooManyPolls(Throwable cause) {
    super(cause);
  }
}
