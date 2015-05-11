package com.shootr.android.ui.presenter;

import android.content.SharedPreferences;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.ChangeEventPhotoInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.domain.interactor.user.GetCheckinStatusInteractor;
import com.shootr.android.domain.interactor.user.PerformCheckinInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.EventDetailView;
import com.shootr.android.util.ErrorMessageFactory;
import com.shootr.android.util.EventTimeFormatter;
import com.shootr.android.util.WatchersTimeFormatter;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.String;
import java.util.ArrayList;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventDetailPresenterTest {

    private static final String EVENT_ID_STUB = "EVENT_ID";
    private EventDetailPresenter presenter;

    @Mock
    Bus bus;
    @Mock
    VisibleEventInfoInteractor eventInfoInteractor;
    @Mock
    ChangeEventPhotoInteractor changeEventPhotoInteractor;
    @Mock
    GetCheckinStatusInteractor getCheckinStatusInteractor;
    @Mock
    PerformCheckinInteractor performCheckinInteractor;

    @Mock
    ErrorMessageFactory errorMessageFactory;
    @Mock
    EventDetailView eventDetailView;

    @Mock
    EventTimeFormatter timeFormatter;

    @Mock
    SessionRepository sessionRepository;

    EventModelMapper eventModelMapper;
    UserModelMapper userModelMapper;

    @Mock SharedPreferences sharedPreferences;
    @Mock WatchersTimeFormatter watchersTimeFormatter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eventModelMapper = new EventModelMapper(new EventTimeFormatter(), sessionRepository);
        userModelMapper= new UserModelMapper();

        presenter = new EventDetailPresenter(bus, eventInfoInteractor,
          changeEventPhotoInteractor, getCheckinStatusInteractor, performCheckinInteractor,
                eventModelMapper, userModelMapper, errorMessageFactory, watchersTimeFormatter);

        presenter.setView(eventDetailView);
    }

    @Test
    public void shouldShowCheckinOnInitializedWhenUserWatchingEventAndNotCheckedIn() {
        setupEventInfoCallbacks(eventInfoWithUserWatching());
        setupCheckinStatusCallbacks(null);

        presenter.initialize(eventDetailView, EVENT_ID_STUB);

        verify(eventDetailView, times(2)).showCheckin();
    }

    @Test
    public void shouldNotShowCheckinOnInitializedWhenUserWatchingEventAndCheckedIn() {
        setupEventInfoCallbacks(eventInfoWithUserWatching());
        setupCheckinStatusCallbacks(EVENT_ID_STUB);

        presenter.initialize(eventDetailView, EVENT_ID_STUB);

        verify(eventDetailView, times(2)).showCheckin();
    }

    //region Setups and stubs
    private void setupEventInfoCallbacks(final EventInfo eventInfo) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                VisibleEventInfoInteractor.Callback callback = (VisibleEventInfoInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(eventInfo);
                return null;
            }
        }).when(eventInfoInteractor).obtainEventInfo(anyString(), any(VisibleEventInfoInteractor.Callback.class));
    }

    private void setupCheckinStatusCallbacks(final String eventCheckedIn) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<String> callback = (Interactor.Callback<String>) invocation.getArguments()[0];
                callback.onLoaded(eventCheckedIn);
                return null;
            }
        }).when(getCheckinStatusInteractor).loadCheckinStatus(any(GetCheckinStatusInteractor.Callback.class));
    }

    private EventInfo eventInfoWithUserNotWatching() {
        EventInfo eventInfo = new EventInfo();
        Event event = eventWithStartDate();
        User user = userWithIdUser();
        event.setAuthorId(user.getIdUser());
        eventInfo.setEvent(event);
        eventInfo.setWatchers(new ArrayList<User>());
        return eventInfo;
    }

    private Event eventWithStartDate() {
        Event event = new Event();
        event.setStartDate(new Date());
        return event;
    }

    private User userWithIdUser() {
        User user = new User();
        user.setIdUser("USER_ID");
        return user;
    }

    private EventInfo eventInfoWithUserWatching() {
        EventInfo eventInfo = new EventInfo();
        Event event = eventWithStartDate();
        User user = userWithIdUser();
        user.setJoinEventDate(0L);
        event.setAuthorId(user.getIdUser());
        eventInfo.setEvent(event);
        eventInfo.setWatchers(new ArrayList<User>());
        eventInfo.setCurrentUserWatching(user);
        return eventInfo;
    }
//endregion
}