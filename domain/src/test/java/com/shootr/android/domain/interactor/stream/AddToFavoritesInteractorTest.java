package com.shootr.android.domain.interactor.stream;

import com.shootr.android.domain.Favorite;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.executor.TestPostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.FavoriteRepository;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddToFavoritesInteractorTest {

    public static final String ID_STREAM = "id_stream";
    @Mock Interactor.CompletedCallback callback;
    @Mock FavoriteRepository localFavoriteRepository;
    @Mock FavoriteRepository remoteFavoriteRepository;
    @Mock Favorite favorite;

    private com.shootr.android.domain.interactor.stream.AddToFavoritesInteractor addToFavoritesInteractor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        addToFavoritesInteractor = new com.shootr.android.domain.interactor.stream.AddToFavoritesInteractor(localFavoriteRepository, remoteFavoriteRepository,
          interactorHandler, postExecutionThread);
    }

    @Test
    public void shouldCallCompletedCallbackWhenAddToFavorites(){
        addToFavoritesInteractor.addToFavorites(ID_STREAM, callback);
        verify(callback).onCompleted();
    }

    @Test
    public void shouldAddFavoriteToLocal(){
        addToFavoritesInteractor.addToFavorites(ID_STREAM, callback);
        verify(localFavoriteRepository).putFavorite(any(Favorite.class));
    }

    @Test
    public void shouldAddFavoriteToRemote(){
        addToFavoritesInteractor.addToFavorites(ID_STREAM, callback);
        verify(remoteFavoriteRepository).putFavorite(any(Favorite.class));
    }

    @Test
    public void shouldAddFavoriteWithOrderThreeWhenLocalRepositoryReturnsTwoFavorites(){
        when(localFavoriteRepository.getFavorites()).thenReturn(twoFavorites());
        addToFavoritesInteractor.addToFavorites(ID_STREAM, callback);
        verify(localFavoriteRepository).putFavorite(favoriteWithOrder(2));
    }

    @Test
    public void shouldAddFavoriteWithOrderThreeWhenLocalRepositoryReturnsTwoFavoritesWithInverseOrder() {
        when(localFavoriteRepository.getFavorites()).thenReturn(twoFavoritesReversed());
        addToFavoritesInteractor.addToFavorites(ID_STREAM, callback);
        verify(localFavoriteRepository).putFavorite(favoriteWithOrder(2));
    }

    @Test
    public void shouldAddFavoriteWithOrderZeroWhenLocalRepositoryReturnsEmpty(){
        when(localFavoriteRepository.getFavorites()).thenReturn(empty());
        addToFavoritesInteractor.addToFavorites(ID_STREAM, callback);
        verify(localFavoriteRepository).putFavorite(favoriteWithOrder(0));
    }

    @Test
    public void shouldNotifyCompletedBeforePutFavoriteInRemote(){
        addToFavoritesInteractor.addToFavorites(ID_STREAM, callback);

        InOrder inOrder = inOrder(callback, remoteFavoriteRepository);
        inOrder.verify(callback).onCompleted();
        inOrder.verify(remoteFavoriteRepository).putFavorite(any(Favorite.class));
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
