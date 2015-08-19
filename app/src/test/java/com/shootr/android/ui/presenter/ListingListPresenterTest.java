package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.android.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.android.domain.interactor.stream.GetUserListingStreamsInteractor;
import com.shootr.android.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.model.mappers.StreamResultModelMapper;
import com.shootr.android.ui.views.ListingView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class ListingListPresenterTest {

    public static final String PROFILE_ID_USER = "profile_id_user";
    public static final String CURRENT_ID_USER = "current_id_user";
    public static final String STREAM_ID = "stream_id";
    public static final String STREAM_AUTHOR_ID = "stream_author_id";
    public static final String STREAM_TITLE = "stream_title";

    @Mock GetUserListingStreamsInteractor getUserListingStreamsInteractor;
    @Mock ListingView listingView;
    @Mock SessionRepository sessionRepository;
    @Mock ListingListPresenter listingListPresenter;
    @Mock StreamResultModelMapper streamResultModelMapper;
    @Mock AddToFavoritesInteractor addToFavoritesInteractor;
    @Mock RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    @Mock GetFavoriteStreamsInteractor getFavoriteStreamInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.streamResultModelMapper = new StreamResultModelMapper(new StreamModelMapper(sessionRepository));
        listingListPresenter = new ListingListPresenter(getUserListingStreamsInteractor,
          addToFavoritesInteractor,
          removeFromFavoritesInteractor,
          getFavoriteStreamInteractor,
          streamResultModelMapper,
          errorMessageFactory);
        listingListPresenter.setView(listingView);
        setupFavoritesInteractorCallbacks();
    }

    protected void setupFavoritesInteractorCallbacks() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<StreamSearchResult>> callback = (Interactor.Callback) invocation.getArguments()[0];
                callback.onLoaded(Collections.<StreamSearchResult>emptyList());

                return null;
            }
        }).when(getFavoriteStreamInteractor).loadFavoriteStreamsFromLocalOnly(any(Interactor.Callback.class));
    }

    @Test
    public void shouldShowContentIfUserHasCreatedStreams() throws Exception {
        setupGetUserListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, CURRENT_ID_USER);

        verify(listingView).showContent();
    }

    @Test
    public void shouldShowStreamsIfUserHasCreatedStreams() throws Exception {
        setupGetUserListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, CURRENT_ID_USER);

        verify(listingView).renderStreams(anyList());
    }

    @Test
    public void shouldHideContentIfUserHasNoCreatedStreams() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, CURRENT_ID_USER);

        verify(listingView).hideContent();
    }

    @Test
    public void shouldShowNoStreamsIfUserHasNoCreatedStreams() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, CURRENT_ID_USER);

        verify(listingView).showEmpty();
    }

    private List<StreamSearchResult> emptyStreamsList() {
        return Collections.emptyList();
    }

    private List<StreamSearchResult> userStreams() {
        return Arrays.asList(getStreamSearchResult());
    }

    private StreamSearchResult getStreamSearchResult() {
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        Stream stream = new Stream();
        stream.setId(STREAM_ID);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        stream.setTitle(STREAM_TITLE);
        streamSearchResult.setStream(stream);
        streamSearchResult.setWatchersNumber(1);
        return null;
    }

    private void setupGetUserListingCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetUserListingStreamsInteractor.Callback callback =
                  (GetUserListingStreamsInteractor.Callback) invocation.getArguments()[0];
                callback.onLoaded(userStreams());
                return null;
            }
        }).when(getUserListingStreamsInteractor).loadUserListingStreams(any(Interactor.Callback.class), anyString());
    }

    private void setupUserWithoutListingCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetUserListingStreamsInteractor.Callback callback =
                  (GetUserListingStreamsInteractor.Callback) invocation.getArguments()[0];
                callback.onLoaded(emptyStreamsList());
                return null;
            }
        }).when(getUserListingStreamsInteractor).loadUserListingStreams(any(Interactor.Callback.class), anyString());
    }
}
