package com.shootr.android.ui.presenter;

import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.User;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.ChangeEventPhotoInteractor;
import com.shootr.android.domain.interactor.event.UpdateStatusInteractor;
import com.shootr.android.domain.interactor.event.VisibleEventInfoInteractor;
import com.shootr.android.domain.interactor.user.GetCheckinStatusInteractor;
import com.shootr.android.domain.interactor.user.PerformCheckinInteractor;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.EventDetailView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class EventDetailPresenterTest {

    private static final long EVENT_ID_STUB = 1L;
    private EventDetailPresenter presenter;

    @Mock Bus bus;
    @Mock VisibleEventInfoInteractor eventInfoInteractor;
    @Mock UpdateStatusInteractor watchingStatusInteractor;
    @Mock ChangeEventPhotoInteractor changeEventPhotoInteractor;
    @Mock GetCheckinStatusInteractor getCheckinStatusInteractor;
    @Mock PerformCheckinInteractor performCheckinInteractor;
    @Mock EventModelMapper eventModelMapper;
    @Mock UserModelMapper userModelMapper;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock EventDetailView eventDetailView;

    @Mock User user;

    @Before public void setUp(){
        MockitoAnnotations.initMocks(this);
        presenter = new EventDetailPresenter(bus, eventInfoInteractor, watchingStatusInteractor,
                changeEventPhotoInteractor, getCheckinStatusInteractor, performCheckinInteractor,
                eventModelMapper, userModelMapper, errorMessageFactory);

        presenter.setView(eventDetailView);
    }

    @Test public void shouldShowCheckinOnInitializedWhenUserWatchingEventAndNotCheckedIn(){
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                VisibleEventInfoInteractor.Callback callback = (VisibleEventInfoInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(eventInfoWithUserWatching());
                return null;
            }
        }).when(eventInfoInteractor).obtainEventInfo(anyLong(), any(VisibleEventInfoInteractor.Callback.class));

        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<Boolean> callback = (Interactor.Callback<Boolean>) invocation.getArguments()[1];
                callback.onLoaded(false);
                return null;
            }
        }).when(getCheckinStatusInteractor).loadCheckinStatus(Matchers.<Interactor.Callback<Boolean>>any());

        presenter.initialize(eventDetailView, EVENT_ID_STUB);

        verify(eventDetailView).showCheckin();
    }

    @Test public void shouldNotShowCheckinOnInitializedWhenUserWatchingEventAndCheckedIn(){
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                VisibleEventInfoInteractor.Callback callback = (VisibleEventInfoInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(eventInfoWithUserWatching());
                return null;
            }
        }).when(eventInfoInteractor).obtainEventInfo(anyLong(), any(VisibleEventInfoInteractor.Callback.class));

        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<Boolean> callback = (Interactor.Callback<Boolean>) invocation.getArguments()[0];
                callback.onLoaded(true);
                return null;
            }
        }).when(getCheckinStatusInteractor).loadCheckinStatus(Matchers.<Interactor.Callback<Boolean>>any());

        presenter.initialize(eventDetailView, EVENT_ID_STUB);

        verify(eventDetailView, never()).showCheckin();
    }

    @Test public void shouldNotShowCheckinOnInitializedWhenUserNotWatchingEventAndNotCheckedIn(){
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                VisibleEventInfoInteractor.Callback callback = (VisibleEventInfoInteractor.Callback) invocation.getArguments()[1];
                callback.onLoaded(eventInfoWithUserWatching());
                return null;
            }
        }).when(eventInfoInteractor).obtainEventInfo(anyLong(), any(VisibleEventInfoInteractor.Callback.class));

        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<Boolean> callback = (Interactor.Callback<Boolean>) invocation.getArguments()[0];
                callback.onLoaded(true);
                return null;
            }
        }).when(getCheckinStatusInteractor).loadCheckinStatus(Matchers.<Interactor.Callback<Boolean>>any());

        presenter.initialize(eventDetailView, EVENT_ID_STUB);

        verify(eventDetailView, never()).showCheckin();
    }

    private EventInfo eventInfoWithUserWatching() {
        EventInfo eventInfo = new EventInfo();
        eventInfo.setCurrentUserWatching(new User());
        return eventInfo;
    }
}
