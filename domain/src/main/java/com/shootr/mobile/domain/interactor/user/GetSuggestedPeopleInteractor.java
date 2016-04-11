package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.SuggestedPeople;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GetSuggestedPeopleInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final UserRepository remoteUserRepository;
    private final UserRepository localUserRepository;
    private final LocaleProvider localeProvider;

    private Callback<List<SuggestedPeople>> callback;
    private ErrorCallback errorCallback;

    @Inject
    public GetSuggestedPeopleInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Remote UserRepository remoteUserRepository, @Local UserRepository localUserRepository,
      LocaleProvider localeProvider) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localUserRepository = localUserRepository;
        this.localeProvider = localeProvider;
    }

    public void loadSuggestedPeople(Callback<List<SuggestedPeople>> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            loadSuggestedPeople();
        } catch (ServerCommunicationException e) {
            notifyError(e);
        }
    }

    private void loadSuggestedPeople() {
        List<SuggestedPeople> localSuggestions = localUserRepository.getSuggestedPeople(localeProvider.getLocale());
        if (localSuggestions.isEmpty()) {
            List<SuggestedPeople> remoteSuggestions = remoteUserRepository.getSuggestedPeople(localeProvider.getLocale());
            filterAndCallback(remoteSuggestions);
        } else {
            filterAndCallback(localSuggestions);
        }
    }

    private void filterAndCallback(List<SuggestedPeople> suggestions) {
        List<SuggestedPeople> usersNotFollowed = filterUsersNotFollowed(suggestions);
        notifyResult(usersNotFollowed);
    }

    private List<SuggestedPeople> filterUsersNotFollowed(List<SuggestedPeople> suggestions) {
        List<User> people = localUserRepository.getPeople();
        List<SuggestedPeople> notFollowed = new ArrayList<>();
        for (SuggestedPeople suggestion : suggestions) {
            if (!people.contains(suggestion.getUser()) && !notFollowed.contains(suggestion)) {
                notFollowed.add(suggestion);
            }
        }
        return notFollowed;
    }

    private void notifyResult(final List<SuggestedPeople> suggestedPeople) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(suggestedPeople);
            }
        });
    }

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
}
