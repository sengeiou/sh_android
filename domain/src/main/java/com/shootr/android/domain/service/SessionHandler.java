package com.shootr.android.domain.service;

import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import javax.inject.Inject;

public class SessionHandler {

    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;

    @Inject public SessionHandler(SessionRepository sessionRepository, @Local UserRepository localUserRepository) {
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
    }

    public boolean hasSession() {
        if (hasSessionActive()) {
            return true;
        } else {
            restoreStoredSession();
            return hasSessionActive();
        }
    }

    private boolean hasSessionActive() {
        return sessionRepository.getCurrentUser() != null
          && sessionRepository.getCurrentUserId() > 0
          && sessionRepository.getSessionToken() != null;
    }

    private void restoreStoredSession() {
        String sessionToken = sessionRepository.getSessionToken();
        long currentUserId = sessionRepository.getCurrentUserId();
        if (sessionToken != null && currentUserId > 0) {
            User currentUser = localUserRepository.getUserById(currentUserId);
            if (currentUser != null) {
                sessionRepository.setCurrentUser(currentUser);
            }
        }
    }
}
