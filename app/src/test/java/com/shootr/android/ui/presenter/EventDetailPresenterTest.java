package com.shootr.android.ui.presenter;

import android.content.SharedPreferences;
import com.shootr.android.domain.interactor.event.ChangeStreamPhotoInteractor;
import com.shootr.android.domain.interactor.event.GetStreamMediaCountInteractor;
import com.shootr.android.domain.interactor.event.VisibleStreamInfoInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.EventDetailView;
import com.shootr.android.util.ErrorMessageFactory;
import com.shootr.android.util.WatchersTimeFormatter;
import com.squareup.otto.Bus;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EventDetailPresenterTest {

    private EventDetailPresenter presenter;

    @Mock Bus bus;
    @Mock VisibleStreamInfoInteractor eventInfoInteractor;
    @Mock ChangeStreamPhotoInteractor changeStreamPhotoInteractor;

    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock EventDetailView eventDetailView;

    @Mock SessionRepository sessionRepository;

    EventModelMapper eventModelMapper;
    UserModelMapper userModelMapper;

    @Mock SharedPreferences sharedPreferences;
    @Mock WatchersTimeFormatter watchersTimeFormatter;
    @Mock GetStreamMediaCountInteractor eventMediaCountInteractor;

    @Before public void setUp() {
        MockitoAnnotations.initMocks(this);
        eventModelMapper = new EventModelMapper(sessionRepository);
        userModelMapper= new UserModelMapper();

        presenter = new EventDetailPresenter(bus,
          eventInfoInteractor, changeStreamPhotoInteractor,
          eventModelMapper,
          userModelMapper,
          errorMessageFactory,
          watchersTimeFormatter, eventMediaCountInteractor);

        presenter.setView(eventDetailView);
    }
}