package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
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
    private static final String ID_STREAM_NOT_FAVORITE = "stream_not_favorite";

    private RemoveFromFavoritesInteractor interactor;

    @Mock FavoriteRepository localFavoriteRepository;
    @Mock FavoriteRepository remoteFavoriteRepository;
    @Mock Interactor.CompletedCallback callback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        interactor = new RemoveFromFavoritesInteractor(new TestInteractorHandler(),
          new TestPostExecutionThread(),
          localFavoriteRepository,
          remoteFavoriteRepository);
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailIfStreamIsNotFoundInFavorites() throws Exception {
        when(localFavoriteRepository.getFavoriteByStream(ID_STREAM_NOT_FAVORITE)).thenReturn(null);

        interactor.removeFromFavorites(ID_STREAM_FAVORITE, callback);
    }

    @Test
    public void shouldRemoveFavoriteFromLocalRepository() throws Exception {
        when(localFavoriteRepository.getFavoriteByStream(ID_STREAM_FAVORITE)).thenReturn(favorite());

        interactor.removeFromFavorites(ID_STREAM_FAVORITE, callback);

        verify(localFavoriteRepository).removeFavoriteByStream(ID_STREAM_FAVORITE);
    }

    @Test
    public void shouldRemoveFavoriteFromRemoteRepository() throws Exception {
        when(localFavoriteRepository.getFavoriteByStream(ID_STREAM_FAVORITE)).thenReturn(favorite());

        interactor.removeFromFavorites(ID_STREAM_FAVORITE, callback);

        verify(remoteFavoriteRepository).removeFavoriteByStream(ID_STREAM_FAVORITE);
    }

    @Test
    public void shouldCallbackCompletedBeforeRemovingFavoriteFromRemote() throws Exception {
        when(localFavoriteRepository.getFavoriteByStream(ID_STREAM_FAVORITE)).thenReturn(favorite());

        interactor.removeFromFavorites(ID_STREAM_FAVORITE, callback);

        InOrder inOrder = inOrder(callback, remoteFavoriteRepository);
        inOrder.verify(callback).onCompleted();
        inOrder.verify(remoteFavoriteRepository).removeFavoriteByStream(ID_STREAM_FAVORITE);
    }

    @Test
    public void shouldRemoveFavoriteFromLocalBeforeCallbackCompleted() throws Exception {
        when(localFavoriteRepository.getFavoriteByStream(ID_STREAM_FAVORITE)).thenReturn(favorite());

        interactor.removeFromFavorites(ID_STREAM_FAVORITE, callback);

        InOrder inOrder = inOrder(localFavoriteRepository, callback);
        inOrder.verify(localFavoriteRepository).removeFavoriteByStream(ID_STREAM_FAVORITE);
        inOrder.verify(callback).onCompleted();
    }

    private Favorite favorite() {
        Favorite favorite = new Favorite();
        favorite.setIdStream(ID_STREAM_FAVORITE);
        return favorite;
    }
}