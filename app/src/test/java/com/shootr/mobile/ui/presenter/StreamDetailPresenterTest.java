package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.StreamInfo;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.ChangeStreamPhotoInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInfoInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.StreamDetailView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class StreamDetailPresenterTest {

    public static final int FIFTY_PLUS_WATCHERS = 55;
    private static final String SELECTED_STREAM_ID = "selected_stream";
    private static final String SELECTED_STREAM_TITLE = "title";
    private static final String STREAM_AUTHOR_ID = "author";
    private static final String ID_USER = "id_user";
    public static final int THREE_WATCHERS = 3;
    public static final String ID_STREAM = "id_stream";
    public static final int NO_WATCHERS = 0;

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

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
        UserModelMapper userModelMapper = new UserModelMapper(new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils));
        presenter = new StreamDetailPresenter(streamInfoInteractor, changeStreamPhotoInteractor, shareStreamInteractor, followInteractor, unfollowInteractor, streamModelMapper, userModelMapper, errorMessageFactory);
        presenter.setView(streamDetailView);
    }

    @Test public void shouldShowTotalWatchers() throws Exception {
        setupStreamInfoCallback();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).setTotalWatchers(anyInt());
    }

    @Test public void shouldShowTotalWatchersFromParticipantsListIfLessThan50() throws Exception {
        setupStreamInfoCallback();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).setTotalWatchers(THREE_WATCHERS);
    }

    @Test public void shouldShowTotalWatchersFromStreamInfoIfMoreThan50() throws Exception {
        setupStreamInfoWith50PlusParticipantsCallback();

        presenter.initialize(streamDetailView, ID_STREAM);

        verify(streamDetailView).setTotalWatchers(FIFTY_PLUS_WATCHERS);
    }


    public void setupStreamInfoCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                GetStreamInfoInteractor.Callback callback =
                  (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(streamInfoWith3Participants());
                return null;
            }
        }).when(streamInfoInteractor).obtainStreamInfo(anyString(),
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
        }).when(streamInfoInteractor).obtainStreamInfo(anyString(),
          (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private StreamInfo streamInfoWith50plusParticipants() {
        return StreamInfo.builder()
          .stream(streamWith50plusParticipants())
          .watchers(moreThan50watchers())
          .currentUserWatching(new User())
          .numberOfFollowing(NO_WATCHERS)
          .hasMoreParticipants(false)
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

    private List<User> watchers() {
        List<User> participants = new ArrayList<>();
        participants.add(user());
        participants.add(new User());
        return participants;
    }

    private List<User> moreThan50watchers() {
        List<User> participants = new ArrayList<>();
        participants.add(user());
        for (int i = 0; i< 50; i++) {
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

}
