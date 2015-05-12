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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EventDetailPresenterTest {

    private EventDetailPresenter presenter;

    @Mock Bus bus;
    @Mock VisibleEventInfoInteractor eventInfoInteractor;
    @Mock ChangeEventPhotoInteractor changeEventPhotoInteractor;

    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock EventDetailView eventDetailView;

    @Mock EventTimeFormatter timeFormatter;

    @Mock SessionRepository sessionRepository;

    EventModelMapper eventModelMapper;
    UserModelMapper userModelMapper;

    @Mock SharedPreferences sharedPreferences;
    @Mock WatchersTimeFormatter watchersTimeFormatter;

    @Before public void setUp() {
        MockitoAnnotations.initMocks(this);
        eventModelMapper = new EventModelMapper(new EventTimeFormatter(), sessionRepository);
        userModelMapper = new UserModelMapper();

        presenter = new EventDetailPresenter(bus,
          eventInfoInteractor,
          changeEventPhotoInteractor,
          eventModelMapper,
          userModelMapper,
          errorMessageFactory,
          watchersTimeFormatter);

        presenter.setView(eventDetailView);
    }
}