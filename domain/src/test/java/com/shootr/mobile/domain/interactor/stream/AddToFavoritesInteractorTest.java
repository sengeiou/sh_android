package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.StreamAlreadyInFavoritesException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.favorite.ExternalFavoriteRepository;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddToFavoritesInteractorTest {

  public static final String ID_STREAM = "id_stream";
  public static final String USER_ID = "userId";
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;
  @Mock InternalFavoriteRepository localFavoriteRepository;
  @Mock ExternalFavoriteRepository remoteFavoriteRepository;
  @Mock SessionRepository sessionRepository;
  @Mock Favorite favorite;
  @Mock BusPublisher busPublisher;

  private AddToFavoritesInteractor addToFavoritesInteractor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    addToFavoritesInteractor =
        new AddToFavoritesInteractor(localFavoriteRepository, remoteFavoriteRepository,
            interactorHandler, postExecutionThread, busPublisher);
  }

  @Test public void shouldAddFavoriteWithOrderThreeWhenLocalRepositoryReturnsTwoFavorites()
      throws StreamAlreadyInFavoritesException {
    when(localFavoriteRepository.getFavorites()).thenReturn(twoFavorites());
    addToFavoritesInteractor.addToFavorites(ID_STREAM, callback, errorCallback);
    verify(localFavoriteRepository).putFavorite(favoriteWithOrder(2));
  }

  @Test
  public void shouldAddFavoriteWithOrderThreeWhenLocalRepositoryReturnsTwoFavoritesWithInverseOrder()
      throws StreamAlreadyInFavoritesException {
    when(localFavoriteRepository.getFavorites()).thenReturn(twoFavoritesReversed());
    addToFavoritesInteractor.addToFavorites(ID_STREAM, callback, errorCallback);
    verify(localFavoriteRepository).putFavorite(favoriteWithOrder(2));
  }

  @Test public void shouldAddFavoriteWithOrderZeroWhenLocalRepositoryReturnsEmpty()
      throws StreamAlreadyInFavoritesException {
    when(localFavoriteRepository.getFavorites()).thenReturn(empty());
    addToFavoritesInteractor.addToFavorites(ID_STREAM, callback, errorCallback);
    verify(localFavoriteRepository).putFavorite(favoriteWithOrder(0));
  }

  @Test public void shouldNotifyCompletedAfterPutFavoriteInRemote()
      throws StreamAlreadyInFavoritesException {
    addToFavoritesInteractor.addToFavorites(ID_STREAM, callback, errorCallback);

    InOrder inOrder = inOrder(remoteFavoriteRepository, callback);
    inOrder.verify(remoteFavoriteRepository).putFavorite(any(Favorite.class));
    inOrder.verify(callback).onCompleted();
  }

  @Test public void shouldNotifyErrorWhenRemoteRepositoryThrowsException()
      throws StreamAlreadyInFavoritesException {
    doThrow(StreamAlreadyInFavoritesException.class).
        when(remoteFavoriteRepository).putFavorite(any(Favorite.class));

    addToFavoritesInteractor.addToFavorites(ID_STREAM, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyErrorWhenStreamIsAlreadyInFavorites()
      throws StreamAlreadyInFavoritesException {
    doThrow(StreamAlreadyInFavoritesException.class).
        when(remoteFavoriteRepository).putFavorite(any(Favorite.class));

    addToFavoritesInteractor.addToFavorites(ID_STREAM, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  private List<Favorite> empty() {
    return new ArrayList<>();
  }

  private List<Favorite> twoFavoritesReversed() {
    List<Favorite> twoFavorites = twoFavorites();
    Collections.reverse(twoFavorites);
    return twoFavorites;
  }

  private List<Favorite> twoFavorites() {
    return Arrays.asList(favoriteWithOrder(0), favoriteWithOrder(1));
  }

  private Favorite favoriteWithOrder(int order) {
    Favorite favorite = new Favorite();
    favorite.setOrder(order);
    favorite.setIdStream(ID_STREAM);
    return favorite;
  }
}
