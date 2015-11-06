package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.PerformCheckinInteractor;
import com.shootr.mobile.ui.views.CheckinView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class CheckinPresenterTest {

    private static final String STREAM_ID = "stream";

    @Mock PerformCheckinInteractor performCheckinInteractor;
    @Mock CheckinView checkinView;

    private CheckinPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new CheckinPresenter(performCheckinInteractor);
        presenter.setView(checkinView);
        presenter.setStreamId(STREAM_ID);
    }

    //region Check in
    @Test
    public void shouldNotPerformCheckinWhenCheckinClicked() throws Exception {
        presenter.checkIn();

        verify(performCheckinInteractor, never()).performCheckin(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test
    public void shouldShowCheckinConfirmationWhenCheckinClicked() throws Exception {
        presenter.checkIn();

        verify(checkinView).showCheckinConfirmation();
    }

    @Test
    public void shouldPerformCheckinWhenCheckinConfirmed() throws Exception {
        presenter.confirmCheckin();

        verify(performCheckinInteractor).performCheckin(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test
    public void shouldDisableCheckinButtonWhenCheckinConfirmed() throws Exception {
        presenter.confirmCheckin();

        verify(checkinView).disableCheckinButton();
    }

    // endregion

    //region Post-Checkin behaviour
    @Test
    public void shouldDisableCheckinButtonWhenPerformCheckinCallbacks() throws Exception {
        setupPerformCheckinCallbacksCompleted();

        presenter.confirmCheckin();

        verify(checkinView).disableCheckinButton();
    }

    @Test
    public void shouldShowCheckinErrorWhenPerformCheckinFails() throws Exception {
        setupPerformCheckinCallbacksError();

        presenter.confirmCheckin();

        verify(checkinView).showCheckinError();
    }

    @Test
    public void shouldEnableCheckinButtonWhenPerformCheckinFails() throws Exception {
        setupPerformCheckinCallbacksError();

        presenter.confirmCheckin();

        verify(checkinView).enableCheckinButton();
    }

    //endregion

    private void setupPerformCheckinCallbacksCompleted() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback = (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(performCheckinInteractor).performCheckin(anyString(), anyCallback(), anyErrorCallback());
    }

    private void setupPerformCheckinCallbacksError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback callback = (Interactor.ErrorCallback) invocation.getArguments()[2];
                callback.onError(new ShootrException("test exception") {
                });
                return null;
            }
        }).when(performCheckinInteractor).performCheckin(anyString(), anyCallback(), anyErrorCallback());
    }

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }

    private Interactor.CompletedCallback anyCallback() {
        return any(Interactor.CompletedCallback.class);
    }
}