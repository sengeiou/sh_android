package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
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
          && sessionRepository.getCurrentUserId() != null
          && sessionRepository.getSessionToken() != null;
    }

    private void restoreStoredSession() {
        String sessionToken = sessionRepository.getSessionToken();
        String currentUserId = sessionRepository.getCurrentUserId();
        if (sessionToken != null && currentUserId != null) {
            User currentUser = localUserRepository.getUserById(currentUserId);
            if (currentUser != null) {
                sessionRepository.createSession(currentUserId, sessionToken, currentUser);
            }
        }
    }
}
