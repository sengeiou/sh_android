package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Listing;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.android.domain.interactor.stream.GetCurrentUserListingStreamsInteractor;
import com.shootr.android.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.android.domain.interactor.stream.GetUserListingStreamsInteractor;
import com.shootr.android.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.android.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.StreamModel;
import com.shootr.android.ui.model.StreamResultModel;
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
    public static final Boolean IS_NOT_CURRENT_USER = false;
    public static final String STREAM_ID = "stream_id";
    public static final String STREAM_AUTHOR_ID = "stream_author_id";
    public static final String STREAM_TITLE = "stream_title";
    public static final boolean IS_CURRENT_USER = true;
    public static final int STREAMS_COUNT = 5;

    @Mock GetUserListingStreamsInteractor getUserListingStreamsInteractor;
    @Mock GetCurrentUserListingStreamsInteractor getCurrentUserListingStreamsInteractor;
    @Mock ListingView listingView;
    @Mock SessionRepository sessionRepository;
    @Mock ListingListPresenter listingListPresenter;
    @Mock AddToFavoritesInteractor addToFavoritesInteractor;
    @Mock RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    @Mock GetFavoriteStreamsInteractor getFavoriteStreamInteractor;
    @Mock ShareStreamInteractor shareStreamInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;

    private StreamResultModelMapper streamResultModelMapper;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.streamResultModelMapper = new StreamResultModelMapper(new StreamModelMapper(sessionRepository));
        listingListPresenter = new ListingListPresenter(getUserListingStreamsInteractor,
          getCurrentUserListingStreamsInteractor,
          addToFavoritesInteractor,
          removeFromFavoritesInteractor,
          getFavoriteStreamInteractor,
          shareStreamInteractor,
          streamResultModelMapper, errorMessageFactory);
        listingListPresenter.setView(listingView);
        setupFavoritesInteractorCallbacks();
    }

    protected void setupFavoritesInteractorCallbacks() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<StreamSearchResult>> callback =
                  (Interactor.Callback) invocation.getArguments()[0];
                callback.onLoaded(Collections.<StreamSearchResult>emptyList());

                return null;
            }
        }).when(getFavoriteStreamInteractor).loadFavoriteStreamsFromLocalOnly(any(Interactor.Callback.class));
    }

    @Test public void shouldShowContentIfUserHasHoldingOrFavoriteStreams() throws Exception {
        setupGetUserListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER, STREAMS_COUNT);

        verify(listingView).showContent();
    }

    @Test public void shouldShowHoldingStreamsIfUserHasHoldingStreams() throws Exception {
        setupGetUserListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER, STREAMS_COUNT);

        verify(listingView).renderHoldingStreams(anyList());
    }

    @Test public void shouldShowFavoriteStreamsIfUserHasFavoriteStreams() throws Exception {
        setupUserWithoutListingCallback();
        setupGetUserWithFavoritesListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER, STREAMS_COUNT);

        verify(listingView).renderFavoritedStreams(anyList());
    }

    @Test public void shouldHideContentIfUserHasNoHoldingOrFavoritedStreams() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER, STREAMS_COUNT);

        verify(listingView).hideContent();
    }

    @Test public void shouldShowNoStreamsIfUserHasNoHoldingOrFavoritedStreams() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER, STREAMS_COUNT);

        verify(listingView).showEmpty();
    }

    @Test public void shouldShowSharedStreamWhenShareStreamsCompletedCallback() throws Exception {
        setupShareStreamCompletedCallback();

        listingListPresenter.shareStream(streamModel());

        verify(listingView).showStreamShared();
    }

    @Test public void shouldShowErrorStreamWhenShareStreamsErrorCallback() throws Exception {
        setupShareStreamErrorCallback();

        listingListPresenter.shareStream(streamModel());

        verify(listingView).showError(anyString());
    }

    @Test public void shouldShowEmptyIfStreamsCountIsZero() throws Exception {
        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER, 0);

        verify(listingView).showEmpty();
    }

    private void setupShareStreamErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[2];
                errorCallback.onError(new ShootrException() {
                });
                return null;
            }
        }).when(shareStreamInteractor)
          .shareStream(anyString(), any(Interactor.CompletedCallback.class), anyErrorCallback());
    }

    private void setupShareStreamCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                completedCallback.onCompleted();
                return null;
            }
        }).when(shareStreamInteractor)
          .shareStream(anyString(), any(Interactor.CompletedCallback.class), anyErrorCallback());
    }

    private StreamResultModel streamModel() {
        StreamResultModel streamResultModel = new StreamResultModel();
        StreamModel streamModel = new StreamModel();
        streamModel.setIdStream(STREAM_ID);
        streamResultModel.setStreamModel(streamModel);
        return streamResultModel;
    }

    @Test public void shouldCallGetCurrentUserListingStreamsInteractorIfCurrentUserProfile() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_CURRENT_USER, STREAMS_COUNT);

        verify(getCurrentUserListingStreamsInteractor).loadCurrentUserListingStreams(any(Interactor.Callback.class),
          anyErrorCallback());
    }

    @Test public void shouldCallGetUserListingStreamsInteractorIfAnotherUserProfile() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER, STREAMS_COUNT);

        verify(getUserListingStreamsInteractor).loadUserListingStreams(any(Interactor.Callback.class), anyString());
    }

    private Listing listingWithEmptyHoldingList() {
        return Listing.builder().build();
    }

    private Listing listingWithUserStreams() {
        return Listing.builder()
          .holdingStreams(Arrays.asList(getStreamSearchResult()))
          .build();
    }

    private Listing listingWithFavoriteStreams() {
        Stream stream = getStream();
        return Listing.builder()
          .favoritedStreams(Arrays.asList(getStreamSearchResult()))
          .build();
    }

    private StreamSearchResult getStreamSearchResult() {
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        Stream stream = getStream();
        streamSearchResult.setStream(stream);
        streamSearchResult.setWatchersNumber(1);
        return streamSearchResult;
    }

    private Stream getStream() {
        Stream stream = new Stream();
        stream.setId(STREAM_ID);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        stream.setTitle(STREAM_TITLE);
        return stream;
    }

    private void setupGetUserListingCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetUserListingStreamsInteractor.Callback callback =
                  (GetUserListingStreamsInteractor.Callback) invocation.getArguments()[0];
                callback.onLoaded(listingWithUserStreams());
                return null;
            }
        }).when(getUserListingStreamsInteractor).loadUserListingStreams(any(Interactor.Callback.class), anyString());
    }

    private void setupUserWithoutListingCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetUserListingStreamsInteractor.Callback callback =
                  (GetUserListingStreamsInteractor.Callback) invocation.getArguments()[0];
                callback.onLoaded(listingWithEmptyHoldingList());
                return null;
            }
        }).when(getUserListingStreamsInteractor).loadUserListingStreams(any(Interactor.Callback.class), anyString());
    }

    private void setupGetUserWithFavoritesListingCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetUserListingStreamsInteractor.Callback callback =
                  (GetUserListingStreamsInteractor.Callback) invocation.getArguments()[0];
                callback.onLoaded(listingWithFavoriteStreams());
                return null;
            }
        }).when(getUserListingStreamsInteractor).loadUserListingStreams(any(Interactor.Callback.class), anyString());
    }

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }
}
