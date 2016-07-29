package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.model.stream.Favorite;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.favorite.ExternalFavoriteRepository;
import com.shootr.mobile.domain.repository.favorite.InternalFavoriteRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemoveFromFavoritesInteractorTest {

  private static final String ID_STREAM_FAVORITE = "stream";
  @Mock InternalFavoriteRepository localFavoriteRepository;
  @Mock ExternalFavoriteRepository remoteFavoriteRepository;
  @Mock Interactor.CompletedCallback callback;
  private RemoveFromFavoritesInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    interactor = new RemoveFromFavoritesInteractor(new TestInteractorHandler(),
        new TestPostExecutionThread(), localFavoriteRepository, remoteFavoriteRepository);
  }

  @Test public void shouldRemoveFavoriteFromLocalRepository() throws Exception {
    when(localFavoriteRepository.getFavoriteByStream(ID_STREAM_FAVORITE)).thenReturn(favorite());

    interactor.removeFromFavorites(ID_STREAM_FAVORITE, callback);

    verify(localFavoriteRepository).removeFavoriteByStream(ID_STREAM_FAVORITE);
  }

  @Test public void shouldRemoveFavoriteFromRemoteRepository() throws Exception {
    when(localFavoriteRepository.getFavoriteByStream(ID_STREAM_FAVORITE)).thenReturn(favorite());

    interactor.removeFromFavorites(ID_STREAM_FAVORITE, callback);

    verify(remoteFavoriteRepository).removeFavoriteByStream(ID_STREAM_FAVORITE);
  }

  @Test public void shouldCallbackCompletedAfterRemovingFavoriteFromRemote() throws Exception {
    when(localFavoriteRepository.getFavoriteByStream(ID_STREAM_FAVORITE)).thenReturn(favorite());

    interactor.removeFromFavorites(ID_STREAM_FAVORITE, callback);

    InOrder inOrder = inOrder(callback, remoteFavoriteRepository);
    inOrder.verify(remoteFavoriteRepository).removeFavoriteByStream(ID_STREAM_FAVORITE);
    inOrder.verify(callback).onCompleted();
  }

  @Test public void shouldRemoveFavoriteFromRemoteBeforeCallbackCompleted() throws Exception {
    when(localFavoriteRepository.getFavoriteByStream(ID_STREAM_FAVORITE)).thenReturn(favorite());

    interactor.removeFromFavorites(ID_STREAM_FAVORITE, callback);

    InOrder inOrder = inOrder(remoteFavoriteRepository, callback);
    inOrder.verify(remoteFavoriteRepository).removeFavoriteByStream(ID_STREAM_FAVORITE);
    inOrder.verify(callback).onCompleted();
  }

  private Favorite favorite() {
    Favorite favorite = new Favorite();
    favorite.setIdStream(ID_STREAM_FAVORITE);
    return favorite;
  }
}