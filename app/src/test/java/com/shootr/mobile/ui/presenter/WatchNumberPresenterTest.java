package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.bus.StreamChanged;
import com.shootr.mobile.domain.bus.WatchUpdateRequest;
import com.shootr.mobile.domain.interactor.stream.WatchNumberInteractor;
import com.shootr.mobile.ui.views.WatchNumberView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.lang.reflect.Method;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class WatchNumberPresenterTest {

    private static final Integer[] COUNT_1_PERSON = new Integer[]{0, 1};
    private static final Integer[] COUNT_2_PEOPLE = new Integer[]{0, 2};
    private static final Integer[] COUNT_NOBODY = new Integer[]{0, 0};
    private static final Integer[] NO_STREAM = new Integer[]{0, 0};
    private static final WatchUpdateRequest.Event WATCH_UPDATE_EVENT = null;
    private static final Boolean LOCAL_ONLY = false;
    public static final String STREAM_ID_STUB = "stream_id";

    @Mock WatchNumberInteractor watchNumberInteractor;
    @Mock Bus bus;
    @Mock WatchNumberView watchNumberView;

    private WatchNumberPresenter presenter;
    private WatchUpdateRequest.Receiver watchUpdateReceiver;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new WatchNumberPresenter(bus, watchNumberInteractor);
        presenter.setView(watchNumberView);
        presenter.setIdStream(STREAM_ID_STUB);
        watchUpdateReceiver = presenter;
    }

    @Test public void shouldLoadWatchNumberWhenInitialized() throws Exception {
        presenter.initialize(watchNumberView, STREAM_ID_STUB);

        verify(watchNumberInteractor).loadWatchersNumber(eq(STREAM_ID_STUB), anyBoolean(), any(WatchNumberInteractor.Callback.class));
    }

    @Test public void shouldShowCountInViewWhenOnePersonWatching() throws Exception {
        setupWatchNumberInteractorCallbacks(COUNT_1_PERSON);

        presenter.retrieveData(LOCAL_ONLY);

        verify(watchNumberView).showParticipantsCount(eq(COUNT_1_PERSON));
    }

    @Test public void shouldShowCountTwoInViewWhenTwoPeopleWatching() throws Exception {
        setupWatchNumberInteractorCallbacks(COUNT_2_PEOPLE);

        presenter.retrieveData(LOCAL_ONLY);

        verify(watchNumberView).showParticipantsCount(eq(COUNT_2_PEOPLE));
    }

    @Test public void shouldHideWatchingPeopleCountWhenNobodyWatching() throws Exception {
        setupWatchNumberInteractorCallbacks(COUNT_NOBODY);

        presenter.retrieveData(LOCAL_ONLY);

        verify(watchNumberView).hideWatchingPeopleCount();
    }

    @Test public void shouldHideCountViewWhenNoStream() throws Exception {
        setupWatchNumberInteractorCallbacks(NO_STREAM);

        presenter.retrieveData(LOCAL_ONLY);

        verify(watchNumberView).hideWatchingPeopleCount();
    }

    @Test public void shouldLoadWatchNumberWhenWatchUpdateRequestReceived() throws Exception {
        watchUpdateReceiver.onWatchUpdateRequest(WATCH_UPDATE_EVENT);

        verify(watchNumberInteractor).loadWatchersNumber(eq(STREAM_ID_STUB), anyBoolean(), any(WatchNumberInteractor.Callback.class));
    }

    @Test public void shouldLoadWatchNumberWhenPresenterResumedAfterPause() throws Exception {
        presenter.pause();
        presenter.resume();

        verify(watchNumberInteractor).loadWatchersNumber(eq(STREAM_ID_STUB), anyBoolean(), any(WatchNumberInteractor.Callback.class));
    }

    @Test public void shouldWatchUpdateReceiverHaveSubscribeAnnotation() throws Exception {
        String receiverMethodName = WatchUpdateRequest.Receiver.class.getDeclaredMethods()[0].getName();

        Method receiverDeclaredMethod =
          watchUpdateReceiver.getClass().getMethod(receiverMethodName, WatchUpdateRequest.Event.class);
        boolean annotationPresent = receiverDeclaredMethod.isAnnotationPresent(Subscribe.class);
        assertThat(annotationPresent).isTrue();
    }

    private StreamChanged.Event streamChangedStream() {
        return new StreamChanged.Event(STREAM_ID_STUB);
    }

    private StreamChanged.Event exitStream() {
        return new StreamChanged.Event(null);
    }

    private void setupWatchNumberInteractorCallbacks(final Integer[] count) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((WatchNumberInteractor.Callback) invocation.getArguments()[1]).onLoaded(count);
                return null;
            }
        }).when(watchNumberInteractor)
            .loadWatchersNumber(eq(STREAM_ID_STUB), anyBoolean(), any(WatchNumberInteractor.Callback.class));
    }
}