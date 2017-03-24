package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetUserListingStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.model.stream.Listing;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.views.ListingView;
import com.shootr.mobile.util.ErrorMessageFactory;
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
    public static final String ANOTHER_USER_ID = "another_user";

    @Mock GetUserListingStreamsInteractor getUserListingStreamsInteractor;
    @Mock ListingView listingView;
    @Mock SessionRepository sessionRepository;
    @Mock ListingListPresenter listingListPresenter;
    @Mock AddToFavoritesInteractor addToFavoritesInteractor;
    @Mock RemoveFromFavoritesInteractor removeFromFavoritesInteractor;
    @Mock GetFavoriteStreamsInteractor getFavoriteStreamInteractor;
    @Mock RemoveStreamInteractor removeStreamInteractor;
    @Mock ShareStreamInteractor shareStreamInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;

    private StreamResultModelMapper streamResultModelMapper;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.streamResultModelMapper = new StreamResultModelMapper(new StreamModelMapper(sessionRepository));
        listingListPresenter = new ListingListPresenter(getUserListingStreamsInteractor,
          addToFavoritesInteractor,
          removeFromFavoritesInteractor,
          getFavoriteStreamInteractor,
          shareStreamInteractor,
          removeStreamInteractor,
          streamResultModelMapper,
          errorMessageFactory);
        listingListPresenter.setView(listingView);
        setupFavoritesInteractorCallbacks();
    }

    @Test public void shouldShowContentIfUserHasHoldingOrFavoriteStreams() throws Exception {
        setupGetUserListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER);

        verify(listingView).showContent();
    }

    @Test public void shouldShowHoldingStreamsIfUserHasHoldingStreams() throws Exception {
        setupGetUserListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER);

        verify(listingView).renderHoldingStreams(anyList());
    }

    @Test public void shouldShowFavoriteStreamsIfUserHasFavoriteStreams() throws Exception {
        setupUserWithoutListingCallback();
        setupGetUserWithFavoritesListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER);

        verify(listingView).renderFavoritedStreams(anyList());
    }

    @Test public void shouldHideContentIfUserHasNoHoldingOrFavoritedStreams() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER);

        verify(listingView).hideContent();
    }

    @Test public void shouldShowNoStreamsIfUserHasNoHoldingOrFavoritedStreams() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER);

        verify(listingView).showEmpty();
    }

    @Test public void shouldShowSectionTitlesIfListingIncludesBothSections() throws Exception {
        Listing listingBothSections = Listing.builder() //
          .favoritedStreams(Collections.singletonList(getStreamSearchResult())) //
          .holdingStreams(Collections.singletonList(getStreamSearchResult())) //
          .build();
        setupGetUserListingCallbacks(listingBothSections);

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER);

        verify(listingView).showSectionTitles();
    }

    @Test public void shouldShowSectionTitlesIfListingIncludesHoldingOnly() throws Exception {
        Listing listingHolding = Listing.builder() //
          .holdingStreams(Collections.singletonList(getStreamSearchResult())) //
          .build();
        setupGetUserListingCallbacks(listingHolding);

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER);

        verify(listingView).showSectionTitles();
    }

    @Test public void shouldShowSharedStreamWhenShareStreamsCompletedCallback() throws Exception {
        setupShareStreamCompletedCallback();

        listingListPresenter.shareStream(streamResultModel());

        verify(listingView).showStreamShared();
    }

    @Test public void shouldShowErrorStreamWhenShareStreamsErrorCallback() throws Exception {
        setupShareStreamErrorCallback();

        listingListPresenter.shareStream(streamResultModel());

        verify(listingView).showError(anyString());
    }

    @Test public void shouldCallGetUserListingStreamsInteractorIfCurrentUserProfile() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_CURRENT_USER);

        verify(getUserListingStreamsInteractor).loadUserListingStreams(any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class),
          anyString());
    }

    @Test public void shouldCallGetUserListingStreamsInteractorIfAnotherUserProfile() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER);

        verify(getUserListingStreamsInteractor).loadUserListingStreams(any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class),
          anyString());
    }

    @Test public void shouldShowCurrentUserContextMenuWithAddFavoriteIfIsCurrentUserAndItsMyStreamAndIsFavorite()
      throws Exception {
        setupGetUserWithFavoritesListingCallback();
        setupFavoritesInteractorCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_CURRENT_USER);
        listingListPresenter.openContextualMenu(streamResultModel());

        verify(listingView).showCurrentUserContextMenuWithoutAddFavorite(any(StreamResultModel.class));
    }

    @Test public void shouldShowCurrentUserContextMenuWithouAddFavoriteIfIsCurrentUserAndItsNotMyStreamAndIsFavorite()
      throws Exception {
        setupGetUserWithFavoritesListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_CURRENT_USER);
        listingListPresenter.openContextualMenu(streamResultModel());

        verify(listingView).showCurrentUserContextMenuWithAddFavorite(any(StreamResultModel.class));
    }

    @Test public void shouldShowContextMenuWithAddFavoriteIfIsCurrentUserAndItsNotMyStream() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_CURRENT_USER);
        listingListPresenter.openContextualMenu(notMyStreamResultModel());

        verify(listingView).showContextMenuWithAddFavorite(any(StreamResultModel.class));
    }

    @Test public void shouldShowContextMenuWithAddFavoriteIfIsNotCurrentUser() throws Exception {
        setupUserWithoutListingCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER);
        listingListPresenter.openContextualMenu(any(StreamResultModel.class));

        verify(listingView).showContextMenuWithAddFavorite(any(StreamResultModel.class));
    }

    @Test public void shouldShowContextMenuWithoutAddFavoriteIfIsCurrentUserAndItsNotMyStream() throws Exception {
        setupFavoritesInteractorCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_CURRENT_USER);
        listingListPresenter.openContextualMenu(getStreamResultModel());

        verify(listingView).showContextMenuWithoutAddFavorite(any(StreamResultModel.class));
    }

    @Test public void shouldShowContextMenuWithoutAddFavoriteIfIsNotCurrentUser() throws Exception {
        setupFavoritesInteractorCallback();

        listingListPresenter.initialize(listingView, PROFILE_ID_USER, IS_NOT_CURRENT_USER);
        listingListPresenter.openContextualMenu(getStreamResultModel());

        verify(listingView).showContextMenuWithoutAddFavorite(any(StreamResultModel.class));
    }

    @Test public void shouldAskConfirmationWhenRemoveStream() throws Exception {
        setupGetUserListingCallback();

        listingListPresenter.remove(STREAM_ID);

        verify(listingView).askRemoveStreamConfirmation();
    }

    @Test public void shouldCallRemoveStreamInteractorWhenRemoveStream() throws Exception {
        setupGetUserListingCallback();

        listingListPresenter.remove(STREAM_ID);
        listingListPresenter.removeStream();

        verify(removeStreamInteractor).removeStream(anyString(),
          any(Interactor.CompletedCallback.class),
          anyErrorCallback());
    }

    @Test public void shouldShowAddButtonIfIsCurrentUserListing() throws Exception {
        listingListPresenter.initialize(listingView, PROFILE_ID_USER, true);

        verify(listingView).showAddStream();
    }

    @Test public void shouldHideAddButtonIfIsAnotherUserListing() throws Exception {
        listingListPresenter.initialize(listingView, PROFILE_ID_USER, false);

        verify(listingView).hideAddStream();
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

    protected void setupFavoritesInteractorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<StreamSearchResult>> callback =
                  (Interactor.Callback) invocation.getArguments()[0];
                callback.onLoaded(Arrays.asList(getStreamSearchResult()));

                return null;
            }
        }).when(getFavoriteStreamInteractor).loadFavoriteStreamsFromLocalOnly(any(Interactor.Callback.class));
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

    private StreamResultModel streamResultModel() {
        StreamResultModel streamResultModel = new StreamResultModel();
        StreamModel streamModel = new StreamModel();
        streamModel.setIdStream(STREAM_ID);
        streamModel.setAuthorId(PROFILE_ID_USER);
        streamModel.setRemoved(false);
        streamResultModel.setStreamModel(streamModel);
        return streamResultModel;
    }

    private StreamResultModel notMyStreamResultModel() {
        StreamResultModel streamResultModel = new StreamResultModel();
        StreamModel streamModel = new StreamModel();
        streamModel.setIdStream(STREAM_ID);
        streamModel.setAuthorId(ANOTHER_USER_ID);
        streamResultModel.setStreamModel(streamModel);
        return streamResultModel;
    }

    private Listing listingWithEmptyHoldingList() {
        return Listing.builder().build();
    }

    private Listing listingWithUserStreams() {
        return Listing.builder().holdingStreams(Arrays.asList(getStreamSearchResult())).build();
    }

    private Listing listingWithFavoriteStreams() {
        return Listing.builder().favoritedStreams(Arrays.asList(getStreamSearchResult())).build();
    }

    private StreamSearchResult getStreamSearchResult() {
        StreamSearchResult streamSearchResult = new StreamSearchResult();
        Stream stream = getStream();
        streamSearchResult.setStream(stream);
        return streamSearchResult;
    }

    private Stream getStream() {
        Stream stream = new Stream();
        stream.setId(STREAM_ID);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        stream.setTitle(STREAM_TITLE);
        return stream;
    }

    private StreamResultModel getStreamResultModel() {
        StreamResultModel streamResultModel = new StreamResultModel();
        StreamModel streamModel = new StreamModel();
        streamModel.setIdStream(STREAM_ID);
        streamModel.setAuthorId(STREAM_AUTHOR_ID);
        streamModel.setTitle(STREAM_TITLE);
        streamResultModel.setStreamModel(streamModel);
        streamResultModel.setStreamModel(streamModel);
        return streamResultModel;
    }

    private void setupGetUserListingCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetUserListingStreamsInteractor.Callback callback =
                  (GetUserListingStreamsInteractor.Callback) invocation.getArguments()[0];
                callback.onLoaded(listingWithUserStreams());
                return null;
            }
        }).when(getUserListingStreamsInteractor)
          .loadUserListingStreams(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class), anyString());
    }

    private void setupUserWithoutListingCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetUserListingStreamsInteractor.Callback callback =
                  (GetUserListingStreamsInteractor.Callback) invocation.getArguments()[0];
                callback.onLoaded(listingWithEmptyHoldingList());
                return null;
            }
        }).when(getUserListingStreamsInteractor)
          .loadUserListingStreams(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class), anyString());
    }

    private void setupGetUserWithFavoritesListingCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetUserListingStreamsInteractor.Callback callback =
                  (GetUserListingStreamsInteractor.Callback) invocation.getArguments()[0];
                callback.onLoaded(listingWithFavoriteStreams());
                return null;
            }
        }).when(getUserListingStreamsInteractor)
          .loadUserListingStreams(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class), anyString());
    }

    private void setupGetUserListingCallbacks(final Listing resultListing) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetUserListingStreamsInteractor.Callback callback =
                  (GetUserListingStreamsInteractor.Callback) invocation.getArguments()[0];
                callback.onLoaded(resultListing);
                return null;
            }
        }).when(getUserListingStreamsInteractor)
          .loadUserListingStreams(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class), anyString());
    }

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }
}
