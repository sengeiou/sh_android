package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.prefs.CacheTimeKeepAlive;
import com.shootr.mobile.data.prefs.LongPreference;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.GetSuggestedPeopleInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.model.user.SuggestedPeople;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.SuggestedPeopleView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SuggestedPeoplePresenter implements Presenter {

  private final GetSuggestedPeopleInteractor getSuggestedPeopleInteractor;
  private final FollowInteractor followInteractor;
  private final UnfollowInteractor unfollowInteractor;
  private final UserModelMapper userModelMapper;
  private final ErrorMessageFactory errorMessageFactory;
  private final LongPreference cacheTimeKeepAlive;

  private SuggestedPeopleView suggestedPeopleView;
  private List<UserModel> suggestedPeople;
  private Boolean hasBeenPaused = false;

  @Inject public SuggestedPeoplePresenter(GetSuggestedPeopleInteractor getSuggestedPeopleInteractor,
      FollowInteractor followInteractor, UnfollowInteractor unfollowInteractor,
      UserModelMapper userModelMapper, ErrorMessageFactory errorMessageFactory,
      @CacheTimeKeepAlive LongPreference cacheTimeKeepAlive) {
    this.getSuggestedPeopleInteractor = getSuggestedPeopleInteractor;
    this.followInteractor = followInteractor;
    this.unfollowInteractor = unfollowInteractor;
    this.userModelMapper = userModelMapper;
    this.errorMessageFactory = errorMessageFactory;
    this.cacheTimeKeepAlive = cacheTimeKeepAlive;
  }

  public void setView(SuggestedPeopleView suggestedPeopleView) {
    this.suggestedPeopleView = suggestedPeopleView;
  }

  public void initialize(SuggestedPeopleView suggestedPeopleView) {
    setView(suggestedPeopleView);
    obtainSuggestedPeople();
    suggestedPeople = new ArrayList<>();
  }

  private void obtainSuggestedPeople() {
    getSuggestedPeopleInteractor.loadSuggestedPeople(cacheTimeKeepAlive.get(),
        new Interactor.Callback<List<SuggestedPeople>>() {
          @Override public void onLoaded(List<SuggestedPeople> suggestedPeoples) {
            cacheTimeKeepAlive.set(getSuggestedPeopleInteractor.getCacheTimeKeepAlive());
            List<UserModel> users = new ArrayList<>();
            for (SuggestedPeople suggested : suggestedPeoples) {
              users.add(userModelMapper.transform(suggested.getUser()));
            }
            suggestedPeopleView.renderSuggestedPeopleList(users);
            suggestedPeople = users;
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            suggestedPeopleView.showError(errorMessageFactory.getMessageForError(error));
          }
        });
  }

  public void followUser(final UserModel user) {
    followInteractor.follow(user.getIdUser(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
          /* no-op */
      }
    }, new Interactor.ErrorCallback() {
      @Override public void onError(ShootrException error) {
        showErrorInView(error);
        onFollowUpdated(user.getIdUser(), false);
      }
    });
  }

  public void unfollowUser(final UserModel user) {
    unfollowInteractor.unfollow(user.getIdUser(), new Interactor.CompletedCallback() {
      @Override public void onCompleted() {
        /* no-op */
      }
    });
  }

  protected void onFollowUpdated(String idUser, boolean following) {
    for (int i = 0; i < suggestedPeople.size(); i++) {
      UserModel userModel = suggestedPeople.get(i);
      if (userModel.getIdUser().equals(idUser)) {
        userModel.setFollowing(following);
        suggestedPeopleView.refreshSuggestedPeople(suggestedPeople);
        break;
      }
    }
  }

  private void showErrorInView(ShootrException error) {
    suggestedPeopleView.showError(errorMessageFactory.getMessageForError(error));
  }

  @Override public void resume() {
    if (hasBeenPaused) {
      obtainSuggestedPeople();
    }
  }

  @Override public void pause() {
    hasBeenPaused = true;
  }

  protected void setSuggestedPeople(List<UserModel> suggestedPeople) {
    this.suggestedPeople = suggestedPeople;
  }
}
