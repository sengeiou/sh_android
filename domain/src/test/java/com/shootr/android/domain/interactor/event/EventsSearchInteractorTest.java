package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.LocaleProvider;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;

public class EventsSearchInteractorTest {

    private EventsSearchInteractor interactor;

    @Mock TestInteractorHandler interactorHandler;
    @Mock EventSearchRepository eventSearchRepository;
    @Mock EventRepository eventRepository;
    @Mock PostExecutionThread postExecutionThread;
    @Mock SessionRepository sessionRepository;
    @Mock LocaleProvider localeProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doCallRealMethod().when(interactorHandler).execute(any(Interactor.class));
        interactor = new EventsSearchInteractor(interactorHandler,
          sessionRepository,
          eventSearchRepository, localEventSearchRepository, postExecutionThread, localeProvider);
    }

}