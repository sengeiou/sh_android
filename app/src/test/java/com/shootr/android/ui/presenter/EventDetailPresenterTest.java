package com.shootr.android.ui.presenter;

import android.content.SharedPreferences;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.ChangeEventPhotoInteractor;
import com.shootr.android.domain.interactor.event.UpdateStatusInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.domain.interactor.user.GetCheckinStatusInteractor;
import com.shootr.android.domain.interactor.user.PerformCheckinInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.EventDetailView;
import com.shootr.android.util.ErrorMessageFactory;
import com.shootr.android.util.EventTimeFormatter;
import com.shootr.android.utils.EventTimeFormatterStub;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class EventDetailPresenterTest {

    private static final String EVENT_ID_STUB = "1L";
    private EventDetailPresenter presenter;

    @Mock
    Bus bus;
    @Mock
    VisibleEventInfoInteractor eventInfoInteractor;
    @Mock
    UpdateStatusInteractor watchingStatusInteractor;
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eventModelMapper = new EventModelMapper(new EventTimeFormatterStub(), sessionRepository);
        userModelMapper= new UserModelMapper();

        presenter = new EventDetailPresenter(bus, eventInfoInteractor, watchingStatusInteractor,
                changeEventPhotoInteractor, getCheckinStatusInteractor, performCheckinInteractor,
                eventModelMapper, userModelMapper, errorMessageFactory);

        presenter.setView(eventDetailView);
    }

    @Test
    public void shouldShowCheckinOnInitializedWhenUserWatchingEventAndNotCheckedIn() {
        setupEventInfoCallbacks(eventInfoWithUserWatching());
        setupCheckinStatusCallbacks(false);

        presenter.initialize(eventDetailView, EVENT_ID_STUB);

        verify(eventDetailView).showCheckin();
    }

    @Test
    public void shouldNotShowCheckinOnInitializedWhenUserWatchingEventAndCheckedIn() {
        setupEventInfoCallbacks(eventInfoWithUserWatching());
        setupCheckinStatusCallbacks(true);

        presenter.initialize(eventDetailView, EVENT_ID_STUB);

        verify(eventDetailView, never()).showCheckin();
    }

    @Test
    public void shouldNotShowCheckinOnInitializedWhenUserNotWatchingEventAndNotCheckedIn() {
        setupEventInfoCallbacks(eventInfoWithUserNotWatching());
        setupCheckinStatusCallbacks(false);

        presenter.initialize(eventDetailView, EVENT_ID_STUB);

        verify(eventDetailView, never()).showCheckin();
    }

    @Test
    public void shouldNotShowCheckinOnInitializedWhenUserNotWatchingAndNotCheckedIn() {
        setupEventInfoCallbacks(eventInfoWithUserNotWatching());
        setupCheckinStatusCallbacks(false);

        presenter.initialize(eventDetailView, EVENT_ID_STUB);

        verify(eventDetailView, never()).showCheckin();
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

    private void setupCheckinStatusCallbacks(final boolean isCheckedIn) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<Boolean> callback = (Interactor.Callback<Boolean>) invocation.getArguments()[0];
                callback.onLoaded(isCheckedIn);
                return null;
            }
        }).when(getCheckinStatusInteractor).loadCheckinStatus(any(GetCheckinStatusInteractor.Callback.class));
    }

    private EventInfo eventInfoWithUserNotWatching() {
        EventInfo eventInfo = new EventInfo();
        Event event = eventWithStartAndEndDate();
        User user = userWithIdUser();
        event.setAuthorId(user.getIdUser());
        eventInfo.setEvent(event);
        eventInfo.setWatchers(new ArrayList<User>());
        eventInfo.setCurrentUserWatching(null);
        return eventInfo;
    }

    private Event eventWithStartAndEndDate() {
        Event event = new Event();
        event.setStartDate(new Date());
        event.setEndDate(new Date());
        return event;
    }

    private User userWithIdUser() {
        User user = new User();
        user.setIdUser("1L");
        return user;
    }

    private EventInfo eventInfoWithUserWatching() {
        EventInfo eventInfo = new EventInfo();
        Event event = eventWithStartAndEndDate();
        User user = userWithIdUser();
        event.setAuthorId(user.getIdUser());
        eventInfo.setEvent(event);
        eventInfo.setWatchers(new ArrayList<User>());
        eventInfo.setCurrentUserWatching(user);
        return eventInfo;
    }
//endregion
}