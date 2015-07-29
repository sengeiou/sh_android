package com.shootr.android.ui.presenter;

import com.shootr.android.domain.SuggestedPeople;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.GetSuggestedPeopleInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.SuggestedPeopleView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SuggestedPeoplePresenter implements Presenter {

    private final SessionRepository sessionRepository;
    private final GetSuggestedPeopleInteractor getSuggestedPeopleInteractor;
    private final UserModelMapper userModelMapper;
    private final ErrorMessageFactory errorMessageFactory;
    private SuggestedPeopleView suggestedPeopleView;

    @Inject public SuggestedPeoplePresenter(SessionRepository sessionRepository,
      GetSuggestedPeopleInteractor getSuggestedPeopleInteractor, UserModelMapper userModelMapper,
      ErrorMessageFactory errorMessageFactory) {
        this.sessionRepository = sessionRepository;
        this.getSuggestedPeopleInteractor = getSuggestedPeopleInteractor;
        this.userModelMapper = userModelMapper;
        this.errorMessageFactory = errorMessageFactory;
    }

    protected void setView(SuggestedPeopleView suggestedPeopleView) {
        this.suggestedPeopleView = suggestedPeopleView;
    }

    public void initialize(SuggestedPeopleView suggestedPeopleView) {
        setView(suggestedPeopleView);
        obtainSuggestedPeople();
    }

    private void obtainSuggestedPeople() {
        if(sessionRepository.getCurrentUser().getNumFollowings() != 0) {
            getSuggestedPeopleInteractor.obtainSuggestedPeople(new Interactor.Callback<List<SuggestedPeople>>() {
                @Override public void onLoaded(List<SuggestedPeople> suggestedPeoples) {
                    List<UserModel> users = new ArrayList<>();
                    for (SuggestedPeople suggestedPeople : suggestedPeoples) {
                        users.add(userModelMapper.transform(suggestedPeople.getUser()));
                    }
                    suggestedPeopleView.renderSuggestedPeopleList(users);
                }
            }, new Interactor.ErrorCallback() {
                @Override public void onError(ShootrException error) {
                    suggestedPeopleView.showError(errorMessageFactory.getMessageForError(error));
                }
            });
        }
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
