package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.ShootrEvent;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.shot.ShootrEventRepository;
import com.shootr.mobile.domain.repository.stream.RecentSearchRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.TimeUtils;
import javax.inject.Inject;

public class PutRecentUserInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final UserRepository localUserRepository;
  private final RecentSearchRepository recentSearchRepository;
  private final ShootrEventRepository localShootrEventRepository;
  private final TimeUtils timeUtils;

  private User user;

  @Inject public PutRecentUserInteractor(InteractorHandler interactorHandler,
      @Local UserRepository localUserRepository, RecentSearchRepository recentSearchRepository,
      @Local ShootrEventRepository localShootrEventRepository, TimeUtils timeUtils) {
    this.interactorHandler = interactorHandler;
    this.localUserRepository = localUserRepository;
    this.recentSearchRepository = recentSearchRepository;
    this.localShootrEventRepository = localShootrEventRepository;
    this.timeUtils = timeUtils;
  }

  public void putRecentUser(User user) {
    this.user = user;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      recentSearchRepository.putRecentUser(user, getCurrentTime());
      sendShootrEventUserProfile();
      localUserRepository.putUser(user);
    } catch (ServerCommunicationException error) {
        /* no-op */
    }
  }

  private void sendShootrEventUserProfile() {
    ShootrEvent shootrEvent = new ShootrEvent();
    shootrEvent.setId(user.getIdUser());
    localShootrEventRepository.viewUserProfileEvent(shootrEvent);
  }

  private long getCurrentTime() {
    return timeUtils.getCurrentTime();
  }
}
