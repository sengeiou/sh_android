package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.interactor.Interactor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class AddToFavoritesInteractorTest {

    @Mock Interactor.CompletedCallback callback;

    private AddToFavoritesInteractor addToFavoritesInteractor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        addToFavoritesInteractor = new AddToFavoritesInteractor();
    }

    @Test
    public void shouldCallCompletedCallbackWhenAddToFavorites(){
        addToFavoritesInteractor.addToFavorites("id_event",callback);
        verify(callback).onCompleted();
    }
}
