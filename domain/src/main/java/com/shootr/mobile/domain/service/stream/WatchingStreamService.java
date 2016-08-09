package com.shootr.mobile.domain.service.stream;

import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import java.util.List;
import javax.inject.Inject;

public class WatchingStreamService {

  private final UserRepository localUserRepository;
  private final SessionRepository sessionRepository;

  @Inject public WatchingStreamService(@Local UserRepository localUserRepository,
      SessionRepository sessionRepository) {
    this.localUserRepository = localUserRepository;
    this.sessionRepository = sessionRepository;
  }

  public void markWatchingStream(List<StreamSearchResult> streams) {
    String watchingId = null;
    User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
    if (currentUser != null) {
      watchingId = localUserRepository.getUserById(sessionRepository.getCurrentUserId())
          .getIdWatchingStream();
    }

    if (watchingId == null) {
      return;
    }

    for (StreamSearchResult stream : streams) {
      if (stream.getStream().getId().equals(watchingId)) {
        stream.setIsWatching(true);
      }
    }
  }
}
