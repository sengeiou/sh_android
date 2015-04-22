package com.shootr.android.domain.service.user;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.InvalidCheckinException;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.user.CreateAccountInteractor;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.utils.SecurityUtils;
import java.io.IOException;
import javax.inject.Inject;

public class ShootrUserService {

    private final UserRepository localUserRepository;
    private final SessionRepository sessionRepository;
    private final CheckinGateway checkinGateway;
    private final CreateAccountGateway createAccountGateway;

    @Inject public ShootrUserService(@Local UserRepository localUserRepository, SessionRepository sessionRepository,
      CheckinGateway checkinGateway, CreateAccountGateway createAccountGateway) {
        this.localUserRepository = localUserRepository;
        this.sessionRepository = sessionRepository;
        this.checkinGateway = checkinGateway;
        this.createAccountGateway = createAccountGateway;
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

    public void createAccount(String username, String email, String password) {
        String hashedPassword = SecurityUtils.encodePassword(password);
        try {
            createAccountGateway.performCreateAccount(username, email, hashedPassword);
        } catch (IOException e) {
            throw new InvalidAccountCreationException(e);
        }
    }

}
