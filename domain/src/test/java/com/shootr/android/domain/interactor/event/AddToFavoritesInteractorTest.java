package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class AddToFavoritesInteractorTest {

    @Mock Interactor.CompletedCallback callback;
    @Mock FavoriteRepository localFavoriteRepository;
    @Mock Favorite favorite;

    private AddToFavoritesInteractor addToFavoritesInteractor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        addToFavoritesInteractor = new AddToFavoritesInteractor(localFavoriteRepository, interactorHandler, sessionRepository);
    }

    @Test
    public void shouldCallCompletedCallbackWhenAddToFavorites(){
        addToFavoritesInteractor.addToFavorites("id_event", callback);
        verify(callback).onCompleted();
    }

    @Test
    public void shouldAddFavoriteToLocal(){
        addToFavoritesInteractor.addToFavorites("id_event", callback);
        verify(localFavoriteRepository).putFavorite(any(Favorite.class));
    }

}
