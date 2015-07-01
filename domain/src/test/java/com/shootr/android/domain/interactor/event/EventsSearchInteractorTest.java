package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.TestInteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.LocaleProvider;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;

public class EventsSearchInteractorTest {

    public static final String TITLE = "title";
    public static final String LOCALE = "locale";
    public static final String ID_CHECKED_EVENT = "id_checked_event";
    private EventsSearchInteractor interactor;

    @Mock TestInteractorHandler interactorHandler;
    @Mock EventSearchRepository eventSearchRepository;
    @Mock EventRepository eventRepository;
    @Mock PostExecutionThread postExecutionThread;
    @Mock SessionRepository sessionRepository;
    @Mock LocaleProvider localeProvider;
    @Mock EventsSearchInteractor.Callback callback;
    @Mock Interactor.ErrorCallback errorCallback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        doCallRealMethod().when(interactorHandler).execute(any(Interactor.class));
        interactor = new EventsSearchInteractor(interactorHandler,
          sessionRepository,
          eventSearchRepository, postExecutionThread, localeProvider);
    }

    private List<EventSearchResult> eventSearchResultList() {
        ArrayList<EventSearchResult> events = new ArrayList<>();
        events.add(eventSearchResult());
        return events;
    }

    private EventSearchResult eventSearchResult() {
        EventSearchResult eventSearchResult = new EventSearchResult();
        Event event = new Event();
        event.setTitle(TITLE);
        eventSearchResult.setEvent(event);
        return eventSearchResult;
    }

    private User user() {
        User user = new User();
        user.setIdCheckedEvent(ID_CHECKED_EVENT);
        return user;
    }
}