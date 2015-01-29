package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.WatchRepository;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;

public class EventsSearchInteractorTest {

    private EventsSearchInteractor interactor;
    @Mock private TestInteractorHandler interactorHandler;
    @Mock private EventSearchRepository eventSearchRepository;
    @Mock private EventRepository eventRepository;
    @Mock private PostExecutionThread postExecutionThread;
    @Mock private WatchRepository localWatchRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doCallRealMethod().when(interactorHandler).execute(any(Interactor.class));
        interactor = new EventsSearchInteractor(interactorHandler, eventSearchRepository, eventRepository,
          localWatchRepository, postExecutionThread);
    }

}