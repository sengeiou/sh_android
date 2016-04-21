package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Contributor;
import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamInfo;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.ChangeStreamPhotoInteractor;
import com.shootr.mobile.domain.interactor.stream.GetMutedStreamsInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInfoInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.RestoreStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.GetContributorsInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.StreamDetailView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StreamDetailPresenterTest {

    public static final int FIFTY_PLUS_WATCHERS = 55;
    private static final String SELECTED_STREAM_ID = "selected_stream";
    private static final String SELECTED_STREAM_TITLE = "title";
    private static final String STREAM_AUTHOR_ID = "author";
    private static final String ID_USER = "id_user";
    public static final int THREE_WATCHERS = 3;
    public static final String ID_STREAM = "id_stream";
    public static final int NO_WATCHERS = 0;
    public static final String PICTURE_URL = "picture_url";

    private StreamDetailPresenter presenter;
    @Mock GetStreamInfoInteractor streamInfoInteractor;
    @Mock ChangeStreamPhotoInteractor changeStreamPhotoInteractor;
    @Mock ShareStreamInteractor shareStreamInteractor;
    @Mock FollowInteractor followInteractor;
    @Mock UnfollowInteractor unfollowInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock SessionRepository sessionRepository;
    @Mock DateRangeTextProvider dateRangeTextProvider;
    @Mock TimeUtils timeUtils;
    @Mock StreamDetailView streamDetailView;
    @Mock SelectStreamInteractor selectStreamInteractor;
    @Mock GetMutedStreamsInteractor getMutedStreamsInteractor;
    @Mock MuteInteractor muteInteractor;
    @Mock UnmuteInteractor unmuteInteractor;
    @Mock GetContributorsInteractor getContributorsInteractor;
    @Mock RemoveStreamInteractor removeStreamInteractor;
    @Mock RestoreStreamInteractor restoreStreamInteractor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
        UserModelMapper userModelMapper =
          new UserModelMapper(new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils));
        presenter = new StreamDetailPresenter(streamInfoInteractor,
          changeStreamPhotoInteractor,
          shareStreamInteractor,
          followInteractor,
          unfollowInteractor,
          selectStreamInteractor,
          getMutedStreamsInteractor,
          muteInteractor,
          unmuteInteractor,
          getContributorsInteractor,
          removeStreamInteractor, restoreStreamInteractor, streamModelMapper,
          userModelMapper,
          errorMessageFactory);
        presenter.setView(streamDetailView);
    }

    @Test public void shouldShowTotalWatchers() throws Exception {
        setupStreamInfoCallback();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).setFollowingNumber(anyInt(), anyInt());
    }

    @Test public void shouldShowTotalWatchersFromParticipantsListIfLessThan50() throws Exception {
        setupStreamInfoCallback();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).setFollowingNumber(0, THREE_WATCHERS);
    }

    @Test public void shouldShowTotalWatchersFromStreamInfoIfMoreThan50() throws Exception {
        setupStreamInfoWith50PlusParticipantsCallback();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).setFollowingNumber(0, FIFTY_PLUS_WATCHERS);
    }

    @Test public void shouldOpenEditPhotoIfIAmAuthorAndPhotoIsNull() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(STREAM_AUTHOR_ID);
        setupStreamInfoCallback();

        presenter.initialize(streamDetailView, ID_STREAM);
        presenter.photoClick();

        verify(streamDetailView).showPhotoOptions();
    }

    @Test public void shouldOpenEditPhotoIfIAmAuthorAndPhotoNotNull() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(STREAM_AUTHOR_ID);
        setupStreamInfoCallbackWithPhoto();

        presenter.initialize(streamDetailView, ID_STREAM);
        presenter.photoClick();

        verify(streamDetailView).showPhotoOptions();
    }

    @Test public void shouldNotOpenEditPhotoIfIAmNotAuthorAndPhotoNull() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        setupStreamInfoCallback();

        presenter.initialize(streamDetailView, ID_STREAM);
        presenter.photoClick();

        verify(streamDetailView, never()).showPhotoOptions();
    }

    @Test public void shouldNotOpenEditPhotoIfIAmNotAuthorAndPhotoNotNull() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        setupStreamInfoCallbackWithPhoto();

        presenter.initialize(streamDetailView, ID_STREAM);
        presenter.photoClick();

        verify(streamDetailView, never()).showPhotoOptions();
    }

    @Test public void shouldZoomPhotoIfIAmNotAuthorAndPhotoNotNull() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        setupStreamInfoCallbackWithPhoto();

        presenter.initialize(streamDetailView, ID_STREAM);
        presenter.photoClick();

        verify(streamDetailView).zoomPhoto(PICTURE_URL);
    }

    @Test public void shouldZoomPhotoIfIAmAuthorAndPhotoNotNull() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(STREAM_AUTHOR_ID);
        setupStreamInfoCallbackWithPhoto();

        presenter.initialize(streamDetailView, ID_STREAM);
        presenter.photoClick();

        verify(streamDetailView).showPhotoOptions();
    }

    @Test public void shouldNotZoomPhotoIfIAmNotAuthorAndPhotoNull() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        setupStreamWithoutPictureInfoCallback();

        presenter.initialize(streamDetailView, ID_STREAM);
        presenter.photoClick();

        verify(streamDetailView, never()).zoomPhoto(PICTURE_URL);
    }

    @Test public void shouldNotZoomPhotoIfIAmAuthorAndPhotoNull() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(STREAM_AUTHOR_ID);
        setupStreamInfoCallback();

        presenter.initialize(streamDetailView, ID_STREAM);
        presenter.photoClick();

        verify(streamDetailView, never()).zoomPhoto(PICTURE_URL);
    }

    @Test public void shouldShowMuteCheckedIfStreamIsMuted() throws Exception {
        setupMutedStreamCallback();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).setMuteStatus(true);
    }

    @Test public void shouldShowMuteUncheckedIfStreamIsNotMuted() throws Exception {
        setupNoStreamMutedCallback();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).setMuteStatus(false);
    }

    @Test public void shouldNeverSelectStreamIfNoStreamInOnResume() throws Exception {
        presenter.resume();

        verify(selectStreamInteractor, never()).selectStream(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldGoToStreamDetailOnToolbarClicked() throws Exception {
        presenter.dataInfoClicked();

        verify(streamDetailView).goToStreamDataInfo(any(StreamModel.class));
    }

    @Test public void shouldGoToContributorsActivityAsHolderOncontributorsClicked() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(STREAM_AUTHOR_ID);
        setupStreamInfoCallback();
        presenter.initialize(streamDetailView, ID_STREAM);

        presenter.contributorsClicked();

        verify(streamDetailView).goToContributorsActivityAsHolder(anyString());
    }

    @Test public void shouldGoToContributorsActivityOncontributorsClicked() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        setupStreamInfoCallback();
        presenter.initialize(streamDetailView, ID_STREAM);

        presenter.contributorsClicked();

        verify(streamDetailView).goToContributorsActivity(anyString());
    }

    @Test public void shouldShowContributorsNumberWhenContributorsListSizeIsMoreThanZero() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        setupStreamInfoCallback();
        setupContributorsCallBackWithContributors();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).showContributorsNumber(anyInt());
    }

    @Test public void shouldHideContributorsNumberWhenContributorsListSizeIsZero() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        setupStreamInfoCallback();
        setupContributorsCallBackWithoutContributors();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).hideContributorsNumber();
    }

    @Test public void shouldShowRemoveStreamIfStreamNotRemoved() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(STREAM_AUTHOR_ID);
        setupStreamInfoCallback();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).showRemoveStreamButton();
    }

    @Test public void shouldShowRestoreStreamIfStreamRemoved() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        setupRemovedStreamInfoCallback();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).showRestoreStreamButton();
    }

    @Test public void shouldShowRemoveStreamConfirmationWhenRemoveClicked() throws Exception {
        setupRemovedStreamInfoCallback();

        presenter.removeStream();

        verify(streamDetailView).askRemoveStreamConfirmation();
    }

    private void setupRemovedStreamInfoCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetStreamInfoInteractor.Callback callback =
                  (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(removedStreamInfo());
                return null;
            }
        }).when(streamInfoInteractor)
          .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    public void setupNoStreamMutedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<String>> callback =
                  (Interactor.Callback<List<String>>) invocation.getArguments()[0];
                callback.onLoaded(Collections.<String>emptyList());
                return null;
            }
        }).when(getMutedStreamsInteractor).loadMutedStreamIds(any(Interactor.Callback.class));
    }

    public void setupMutedStreamCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<String>> callback =
                  (Interactor.Callback<List<String>>) invocation.getArguments()[0];
                callback.onLoaded(Arrays.asList(ID_STREAM));
                return null;
            }
        }).when(getMutedStreamsInteractor).loadMutedStreamIds(any(Interactor.Callback.class));
    }

    private void setupStreamWithoutPictureInfoCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetStreamInfoInteractor.Callback callback =
                    (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(streamInfoWithoutPhoto());
                return null;
            }
        }).when(streamInfoInteractor)
            .obtainStreamInfo(anyString(),
                (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
                any(Interactor.ErrorCallback.class));
    }

    private void setupStreamInfoCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetStreamInfoInteractor.Callback callback =
                  (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(streamInfoWith3Participants());
                return null;
            }
        }).when(streamInfoInteractor)
          .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    public void setupStreamInfoCallbackWithPhoto() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetStreamInfoInteractor.Callback callback =
                  (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(streamInfoWithPhoto());
                return null;
            }
        }).when(streamInfoInteractor)
          .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    public void setupStreamInfoWith50PlusParticipantsCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetStreamInfoInteractor.Callback callback =
                  (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(streamInfoWith50plusParticipants());
                return null;
            }
        }).when(streamInfoInteractor)
          .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private void setupContributorsCallBackWithContributors() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetContributorsInteractor.Callback callback =
                  (GetContributorsInteractor.Callback) invocation.getArguments()[2];
                callback.onLoaded(contributorList());
                return null;
            }
        }).when(getContributorsInteractor)
          .obtainContributors(anyString(),
            anyBoolean(),
            any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private void setupContributorsCallBackWithoutContributors() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetContributorsInteractor.Callback callback =
                  (GetContributorsInteractor.Callback) invocation.getArguments()[2];
                callback.onLoaded(Collections.emptyList());
                return null;
            }
        }).when(getContributorsInteractor)
          .obtainContributors(anyString(),
            anyBoolean(),
            any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private StreamInfo streamInfoWith50plusParticipants() {
        return StreamInfo.builder()
          .stream(streamWith50plusParticipants())
          .watchers(moreThan50watchers())
          .currentUserWatching(new User())
          .numberOfFollowing(NO_WATCHERS)
          .hasMoreParticipants(true)
          .isDataComplete(true)
          .build();
    }

    private StreamInfo streamInfoWith3Participants() {
        return StreamInfo.builder()
          .stream(stream())
          .watchers(watchers())
          .currentUserWatching(new User())
          .numberOfFollowing(NO_WATCHERS)
          .hasMoreParticipants(false)
          .isDataComplete(true)
          .build();
    }

    private StreamInfo removedStreamInfo() {
        Stream stream = stream();
        stream.setRemoved(true);
        stream.setAuthorId(ID_USER);
        return StreamInfo.builder()
          .stream(stream)
          .watchers(watchers())
          .currentUserWatching(new User())
          .numberOfFollowing(NO_WATCHERS)
          .hasMoreParticipants(false)
          .isDataComplete(true)
          .build();
    }

    private StreamInfo streamInfoWithPhoto() {
        return StreamInfo.builder()
          .stream(streamWithPhoto())
          .watchers(watchers())
          .currentUserWatching(new User())
          .numberOfFollowing(NO_WATCHERS)
          .hasMoreParticipants(false)
          .isDataComplete(true)
          .build();
    }

    private StreamInfo streamInfoWithoutPhoto() {
        return StreamInfo.builder()
            .stream(streamWithoutPhoto())
            .watchers(watchers())
            .currentUserWatching(new User())
            .numberOfFollowing(NO_WATCHERS)
            .hasMoreParticipants(false)
            .isDataComplete(true)
            .build();
    }

    private List<User> watchers() {
        List<User> participants = new ArrayList<>();
        participants.add(user());
        participants.add(new User());
        return participants;
    }

    private List<User> moreThan50watchers() {
        List<User> participants = new ArrayList<>();
        participants.add(user());
        for (int i = 0; i < 50; i++) {
            participants.add(new User());
        }
        return participants;
    }

    private User user() {
        User user = new User();
        user.setIdUser(ID_USER);
        return user;
    }

    private Stream stream() {
        Stream stream = new Stream();
        stream.setId(SELECTED_STREAM_ID);
        stream.setTitle(SELECTED_STREAM_TITLE);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        stream.setTotalWatchers(NO_WATCHERS);
        stream.setPicture(PICTURE_URL);
        return stream;
    }

    private Stream streamWithPhoto() {
        Stream stream = new Stream();
        stream.setId(SELECTED_STREAM_ID);
        stream.setTitle(SELECTED_STREAM_TITLE);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        stream.setTotalWatchers(NO_WATCHERS);
        stream.setPicture(PICTURE_URL);
        return stream;
    }

    private Stream streamWithoutPhoto() {
        Stream stream = new Stream();
        stream.setId(SELECTED_STREAM_ID);
        stream.setTitle(SELECTED_STREAM_TITLE);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        stream.setTotalWatchers(NO_WATCHERS);
        return stream;
    }

    private Stream streamWith50plusParticipants() {
        Stream stream = new Stream();
        stream.setId(SELECTED_STREAM_ID);
        stream.setTitle(SELECTED_STREAM_TITLE);
        stream.setAuthorId(STREAM_AUTHOR_ID);
        stream.setTotalWatchers(FIFTY_PLUS_WATCHERS);
        return stream;
    }

    private List<Contributor> contributorList() {
        List<Contributor> contributors = new ArrayList<>();
        Contributor contributor = new Contributor();
        contributor.setIdStream(ID_STREAM);
        contributor.setUser(user());
        contributor.setIdUser(ID_USER);
        contributors.add(contributor);
        return contributors;
    }
}
