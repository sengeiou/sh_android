package com.shootr.android.domain.service.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.InvalidCheckinException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.io.IOException;
import javax.inject.Inject;

public class ShootrUserService {

    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;
    private final CheckinGateway checkinGateway;

    @Inject public ShootrUserService(@Local UserRepository localUserRepository, SessionRepository sessionRepository,
      CheckinGateway checkinGateway) {
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
        this.checkinGateway = checkinGateway;
    }

    public void checkInCurrentEvent() {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        if (currentUser.isCheckedIn()) {
            throw new InvalidCheckinException("Can't perform checkin if already checked-in");
        }
        Long visibleEventId = currentUser.getVisibleEventId();
        if (visibleEventId == null) {
            throw new InvalidCheckinException("Can't perform checkin without visible event");
        }

        try {
            checkinGateway.performCheckin(currentUser.getIdUser(), visibleEventId);
        } catch (IOException e) {
            throw new InvalidCheckinException(e);
        }

        currentUser.setCheckedIn(true);
        localUserRepository.putUser(currentUser);
    }

}
