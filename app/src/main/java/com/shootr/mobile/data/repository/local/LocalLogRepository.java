package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.domain.repository.log.LogRepository;
import javax.inject.Inject;

public class LocalLogRepository implements LogRepository {

  private static final String METHOD_NOT_VALID_FOR_REPOSITORY =
      "Method not implemented in remote repository";

  @Inject public LocalLogRepository() {
  }

  @Override public void sendLogs() {
    throw new IllegalStateException(METHOD_NOT_VALID_FOR_REPOSITORY);
  }
}
