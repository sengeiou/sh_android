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

    private static final String ID_EVENT_FAVORITE = "event";
    private static final String ID_EVENT_NOT_FAVORITE = "event_not_favorite";

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
    public void shouldFailIfEventIsNotFoundInFavorites() throws Exception {
        when(localFavoriteRepository.getFavoriteByEvent(ID_EVENT_NOT_FAVORITE)).thenReturn(null);

        interactor.removeFromFavorites(ID_EVENT_FAVORITE, callback);
    }

    @Test
    public void shouldRemoveFavoriteFromLocalRepository() throws Exception {
        when(localFavoriteRepository.getFavoriteByEvent(ID_EVENT_FAVORITE)).thenReturn(favorite());

        interactor.removeFromFavorites(ID_EVENT_FAVORITE, callback);

        verify(localFavoriteRepository).removeFavoriteByEvent(ID_EVENT_FAVORITE);
    }

    @Test
    public void shouldRemoveFavoriteFromRemoteRepository() throws Exception {
        when(localFavoriteRepository.getFavoriteByEvent(ID_EVENT_FAVORITE)).thenReturn(favorite());

        interactor.removeFromFavorites(ID_EVENT_FAVORITE, callback);

        verify(remoteFavoriteRepository).removeFavoriteByEvent(ID_EVENT_FAVORITE);
    }

    @Test
    public void shouldCallbackCompletedBeforeRemovingFavoriteFromRemote() throws Exception {
        when(localFavoriteRepository.getFavoriteByEvent(ID_EVENT_FAVORITE)).thenReturn(favorite());

        interactor.removeFromFavorites(ID_EVENT_FAVORITE, callback);

        InOrder inOrder = inOrder(callback, remoteFavoriteRepository);
        inOrder.verify(callback).onCompleted();
        inOrder.verify(remoteFavoriteRepository).removeFavoriteByEvent(ID_EVENT_FAVORITE);
    }

    @Test
    public void shouldRemoveFavoriteFromLocalBeforeCallbackCompleted() throws Exception {
        when(localFavoriteRepository.getFavoriteByEvent(ID_EVENT_FAVORITE)).thenReturn(favorite());

        interactor.removeFromFavorites(ID_EVENT_FAVORITE, callback);

        InOrder inOrder = inOrder(localFavoriteRepository, callback);
        inOrder.verify(localFavoriteRepository).removeFavoriteByEvent(ID_EVENT_FAVORITE);
        inOrder.verify(callback).onCompleted();
    }

    private Favorite favorite() {
        Favorite favorite = new Favorite();
        favorite.setIdEvent(ID_EVENT_FAVORITE);
        return favorite;
    }
}