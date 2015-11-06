package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.SuggestedPeople;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.SpyCallback;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetSuggestedPeopleInteractorTest {

    @Mock UserRepository localUserRepository;
    @Mock UserRepository remoteUserRepository;
    @Mock Interactor.ErrorCallback errorCallback;
    @Spy SpyCallback<List<SuggestedPeople>> callback = new SpyCallback<>();

    private GetSuggestedPeopleInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestInteractorHandler interactorHandler = new TestInteractorHandler();
        TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetSuggestedPeopleInteractor(interactorHandler,
          postExecutionThread,
          remoteUserRepository,
          localUserRepository);
    }

    @Test public void shouldNotAskForRemoteSuggestionsWhenLocalRepositoryReturnsData() throws Exception {
        setupDoesFollowSomeone();
        when(localUserRepository.getSuggestedPeople()).thenReturn(stubSuggestions());

        interactor.loadSuggestedPeople(callback, errorCallback);

        verify(remoteUserRepository, never()).getSuggestedPeople();
    }

    @Test public void shouldAskForRemoteSuggestionsWhenLocalRepositoryReturnsEmpty() throws Exception {
        setupDoesFollowSomeone();
        when(localUserRepository.getSuggestedPeople()).thenReturn(Collections.<SuggestedPeople>emptyList());

        interactor.loadSuggestedPeople(callback, errorCallback);

        verify(remoteUserRepository).getSuggestedPeople();
    }

    @Test public void shouldCallbackOneUserWhenRepositoryReturnsTwoUsersAndOneIsFollowed() throws Exception {
        setupDoesFollowSomeone();
        when(localUserRepository.getSuggestedPeople()).thenReturn(Arrays.asList(followedSuggestion(),
          notFollowedSuggestion()));

        interactor.loadSuggestedPeople(callback, errorCallback);

        List<SuggestedPeople> result = callback.firstResult();
        assertThat(result).hasSize(1);
    }

    @Test public void shouldCallbackEmptyWhenRepositoryReturnsFollowedUsersOnly() throws Exception {
        setupDoesFollowSomeone();
        when(localUserRepository.getSuggestedPeople()).thenReturn(Arrays.asList(followedSuggestion(),
          followedSuggestion()));

        interactor.loadSuggestedPeople(callback, errorCallback);

        List<SuggestedPeople> result = callback.firstResult();
        assertThat(result).isEmpty();
    }

    @Test public void shouldCallbackWhenNoPeopleFound() throws Exception {
        when(localUserRepository.getPeople()).thenReturn(Collections.<User>emptyList());

        interactor.loadSuggestedPeople(callback, errorCallback);

        verify(remoteUserRepository).getSuggestedPeople();
    }

    protected void setupDoesFollowSomeone() {
        when(localUserRepository.getPeople()).thenReturn(Collections.singletonList(followedUser()));
    }

    private SuggestedPeople followedSuggestion() {
        SuggestedPeople suggestedPeople = new SuggestedPeople();
        suggestedPeople.setUser(followedUser());
        return suggestedPeople;
    }

    private SuggestedPeople notFollowedSuggestion() {
        SuggestedPeople suggestedPeople = new SuggestedPeople();
        suggestedPeople.setUser(notFollowedUser());
        return suggestedPeople;
    }

    private User followedUser() {
        User user = new User();
        user.setIdUser("followed");
        user.setFollowing(true);
        return user;
    }

    private User notFollowedUser() {
        User user = new User();
        user.setIdUser("not_followed");
        user.setFollowing(false);
        return user;
    }

    private List<SuggestedPeople> stubSuggestions() {
        return Collections.singletonList(stubSuggestion());
    }

    private SuggestedPeople stubSuggestion() {
        return new SuggestedPeople();
    }
}