package com.shootr.android.ui.presenter;

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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EventDetailPresenterTest {

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
    @Mock EventDetailView emailLoginView;

    @Before public void setUp(){
        MockitoAnnotations.initMocks(this);
        presenter = new EventDetailPresenter(bus, eventInfoInteractor, watchingStatusInteractor,
                changeEventPhotoInteractor, getCheckinStatusInteractor, performCheckinInteractor,
                eventModelMapper, userModelMapper, errorMessageFactory);

        presenter.setView(emailLoginView);
    }

}
