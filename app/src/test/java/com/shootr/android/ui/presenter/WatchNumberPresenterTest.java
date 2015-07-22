package com.shootr.android.ui.presenter;

import com.shootr.android.domain.bus.StreamChanged;
import com.shootr.android.domain.bus.WatchUpdateRequest;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.event.WatchNumberInteractor;
import com.shootr.android.ui.views.WatchNumberView;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class WatchNumberPresenterTest {

    private static final Integer COUNT_1_PERSON = 1;
    private static final Integer COUNT_2_PEOPLE = 2;
    private static final Integer COUNT_NOBODY = 0;
    private static final WatchUpdateRequest.Stream WATCH_UPDATE_STREAM = null;
    public static final String STREAM_ID_STUB = "stream_id";

    @Mock WatchNumberInteractor watchNumberInteractor;
    @Mock Bus bus;
    @Mock WatchNumberView watchNumberView;

    private WatchNumberPresenter presenter;
    private WatchUpdateRequest.Receiver watchUpdateReceiver;
    private StreamChanged.Receiver streamChangedReceiver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new WatchNumberPresenter(bus, watchNumberInteractor);
        presenter.setView(watchNumberView);
        watchUpdateReceiver = presenter;
        streamChangedReceiver = presenter;
    }

    @Test
    public void shouldLoadWatchNumberWhenInitialized() throws Exception {
        presenter.initialize(watchNumberView, STREAM_ID_STUB);

        verify(watchNumberInteractor).loadWatchNumber(any(WatchNumberInteractor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    @Test
    public void shouldShowCountInViewWhenOnePersonWatching() throws Exception {
        setupWatchNumberInteractorCallbacks(COUNT_1_PERSON);

        presenter.retrieveData();

        verify(watchNumberView).showWatchingPeopleCount(eq(COUNT_1_PERSON));
    }

    @Test
    public void shouldShowCountTwoInViewWhenTwoPeopleWatching() throws Exception {
        setupWatchNumberInteractorCallbacks(COUNT_2_PEOPLE);

        presenter.retrieveData();

        verify(watchNumberView).showWatchingPeopleCount(eq(COUNT_2_PEOPLE));
    }

    @Test
    public void shouldShowCountZeroInViewWhenNobodyWatching() throws Exception {
        setupWatchNumberInteractorCallbacks(COUNT_NOBODY);

        presenter.retrieveData();

        verify(watchNumberView).showWatchingPeopleCount(eq(COUNT_NOBODY));
    }

    @Test
    public void shouldHideCountViewWhenNoStream() throws Exception {
        setupWatchNumberInteractorCallbacks(WatchNumberInteractor.NO_STREAM);

        presenter.retrieveData();

        verify(watchNumberView).hideWatchingPeopleCount();
    }

    @Test
    public void shouldLoadWatchNumberWhenWatchUpdateRequestReceived() throws Exception {
        watchUpdateReceiver.onWatchUpdateRequest(WATCH_UPDATE_STREAM);

        verify(watchNumberInteractor).loadWatchNumber(any(WatchNumberInteractor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    @Test
    public void shouldLoadWatchNumberWhenStreamChanged() throws Exception {
        streamChangedReceiver.onStreamChanged(streamChangedStream());

        verify(watchNumberInteractor).loadWatchNumber(any(WatchNumberInteractor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    @Test
    public void shouldLoadWatchNumberWhenPresenterResumed() throws Exception {
        presenter.resume();

        verify(watchNumberInteractor).loadWatchNumber(any(WatchNumberInteractor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    @Test
    public void shouldHideWatchNumberWhenExitStreamReceivedAlthougInteractorHasntCallback() throws Exception {
        doNothing().when(watchNumberInteractor).loadWatchNumber(any(WatchNumberInteractor.Callback.class), any(Interactor.ErrorCallback.class));

        streamChangedReceiver.onStreamChanged(exitStream());

        verify(watchNumberView).hideWatchingPeopleCount();
    }

    @Test
    public void shouldWatchUpdateReceiverHaveSubscribeAnnotation() throws Exception {
        String receiverMethodName = WatchUpdateRequest.Receiver.class.getDeclaredMethods()[0].getName();

        Method receiverDeclaredMethod = watchUpdateReceiver.getClass().getMethod(receiverMethodName, WatchUpdateRequest.Stream.class);
        boolean annotationPresent = receiverDeclaredMethod.isAnnotationPresent(Subscribe.class);
        assertThat(annotationPresent).isTrue();
    }

    @Test
    public void shouldStreamChangedReceiverHaveSubscribeAnnotation() throws Exception {
        String receiverMethodName = StreamChanged.Receiver.class.getDeclaredMethods()[0].getName();

        Method receiverDeclaredMethod = streamChangedReceiver.getClass().getMethod(receiverMethodName, StreamChanged.Stream.class);
        boolean annotationPresent = receiverDeclaredMethod.isAnnotationPresent(Subscribe.class);
        assertThat(annotationPresent).isTrue();
    }

    private StreamChanged.Stream streamChangedStream() {
        return new StreamChanged.Stream(STREAM_ID_STUB);
    }

    private StreamChanged.Stream exitStream() {
        return new StreamChanged.Stream(null);
    }

    private void setupWatchNumberInteractorCallbacks(final Integer count) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((WatchNumberInteractor.Callback) invocation.getArguments()[0]).onLoaded(count);
                return null;
            }
        }).when(watchNumberInteractor).loadWatchNumber(any(WatchNumberInteractor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

}