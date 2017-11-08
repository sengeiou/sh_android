package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.SpyCallback;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.user.SuggestedPeople;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetSuggestedPeopleInteractorTest {

  public static final String LOCALE = "locale";
  public static final long ANY_DATE = 1501846780374L;
  @Mock UserRepository localUserRepository;
  @Mock UserRepository remoteUserRepository;
  @Mock Interactor.ErrorCallback errorCallback;
  @Mock LocaleProvider localeProvider;
  @Spy SpyCallback<List<SuggestedPeople>> callback = new SpyCallback<>();

  private GetSuggestedPeopleInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
    interactor = spy(new GetSuggestedPeopleInteractor(interactorHandler, postExecutionThread,
        remoteUserRepository, localUserRepository, localeProvider));
    when(localeProvider.getLocale()).thenReturn(LOCALE);
  }

  @Test public void shouldAskForRemoteSuggestionsWhenLocalRepositoryReturnsEmpty()
      throws Exception {
    setupDoesFollowSomeone();
    when(localUserRepository.getSuggestedPeople(localeProvider.getLocale())).thenReturn(nothing());

    interactor.loadSuggestedPeople(cacheKeepAlive(), callback, errorCallback);

    verify(remoteUserRepository).getSuggestedPeople(localeProvider.getLocale());
  }

  private List<SuggestedPeople> nothing() {
    return Collections.<SuggestedPeople>emptyList();
  }

  @Test public void shouldCallbackOneUserWhenRepositoryReturnsTwoUsersAndOneIsFollowed()
      throws Exception {
    setupDoesFollowSomeone();

    when(localUserRepository.getSuggestedPeople(localeProvider.getLocale())).thenReturn(
        Arrays.asList(followedSuggestion(), notFollowedSuggestion()));

    doReturn(false).when(interactor).cacheHasExpired();

    interactor.loadSuggestedPeople(cacheKeepAlive(), callback, errorCallback);

    List<SuggestedPeople> result = callback.firstResult();
    assertThat(result).hasSize(1);
  }

  @Test public void shouldCallbackEmptyWhenRepositoryReturnsFollowedUsersOnly() throws Exception {
    setupDoesFollowSomeone();
    when(localUserRepository.getSuggestedPeople(localeProvider.getLocale())).thenReturn(
        Arrays.asList(followedSuggestion(), followedSuggestion()));

    interactor.loadSuggestedPeople(cacheKeepAlive(), callback, errorCallback);

    List<SuggestedPeople> result = callback.firstResult();
    assertThat(result).isEmpty();
  }

  @Test public void shouldCallbackWhenNoPeopleFound() throws Exception {

    interactor.loadSuggestedPeople(cacheKeepAlive(), callback, errorCallback);

    verify(remoteUserRepository).getSuggestedPeople(localeProvider.getLocale());
  }

  protected void setupDoesFollowSomeone() {
    /* no-op */
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

  private long cacheKeepAlive() {
    return ANY_DATE;
  }
}