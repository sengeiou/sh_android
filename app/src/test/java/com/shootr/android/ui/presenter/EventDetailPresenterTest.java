package com.shootr.android.ui.presenter;

import android.content.SharedPreferences;
import com.shootr.android.domain.interactor.event.ChangeStreamPhotoInteractor;
import com.shootr.android.domain.interactor.event.GetStreamMediaCountInteractor;
import com.shootr.android.domain.interactor.event.VisibleStreamInfoInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.StreamDetailView;
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
    @Mock StreamDetailView streamDetailView;

    @Mock SessionRepository sessionRepository;

    StreamModelMapper streamModelMapper;
    UserModelMapper userModelMapper;

    @Mock SharedPreferences sharedPreferences;
    @Mock WatchersTimeFormatter watchersTimeFormatter;
    @Mock GetStreamMediaCountInteractor eventMediaCountInteractor;

    @Before public void setUp() {
        MockitoAnnotations.initMocks(this);
        streamModelMapper = new StreamModelMapper(sessionRepository);
        userModelMapper= new UserModelMapper();

        presenter = new EventDetailPresenter(bus,
          eventInfoInteractor, changeStreamPhotoInteractor, streamModelMapper,
          userModelMapper,
          errorMessageFactory,
          watchersTimeFormatter, eventMediaCountInteractor);

        presenter.setView(streamDetailView);
    }
}