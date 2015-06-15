package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddToFavoritesInteractorTest {

    public static final String ID_EVENT = "id_event";
    @Mock Interactor.CompletedCallback callback;
    @Mock FavoriteRepository localFavoriteRepository;
    @Mock FavoriteRepository remoteFavoriteRepository;
    @Mock Favorite favorite;

    private AddToFavoritesInteractor addToFavoritesInteractor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        addToFavoritesInteractor = new AddToFavoritesInteractor(localFavoriteRepository, remoteFavoriteRepository,
          interactorHandler);
    }

    @Test
    public void shouldCallCompletedCallbackWhenAddToFavorites(){
        addToFavoritesInteractor.addToFavorites(ID_EVENT, callback);
        verify(callback).onCompleted();
    }

    @Test
    public void shouldAddFavoriteToLocal(){
        addToFavoritesInteractor.addToFavorites(ID_EVENT, callback);
        verify(localFavoriteRepository).putFavorite(any(Favorite.class));
    }

    @Test
    public void shouldAddFavoriteToRemote(){
        addToFavoritesInteractor.addToFavorites(ID_EVENT, callback);
        verify(remoteFavoriteRepository).putFavorite(any(Favorite.class));
    }

    @Test
    public void shouldAddFavoriteWithOrderThreeWhenLocalRepositoryReturnTwoFavorites(){
        when(localFavoriteRepository.getFavorites()).thenReturn(twoFavorites());
        addToFavoritesInteractor.addToFavorites(ID_EVENT, callback);
        verify(localFavoriteRepository).putFavorite(favoriteWithOrder(2));
    }

    private List<Favorite> twoFavorites() {
        return Arrays.asList(favoriteWithOrder(0), favoriteWithOrder(1));
    }

    private Favorite favoriteWithOrder(int order) {
        Favorite favorite = new Favorite();
        favorite.setOrder(order);
        favorite.setIdEvent(ID_EVENT);
        return favorite;
    }
}
