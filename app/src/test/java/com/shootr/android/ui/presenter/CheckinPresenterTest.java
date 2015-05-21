package com.shootr.android.ui.presenter;

import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.GetCheckinStatusInteractor;
import com.shootr.android.domain.interactor.user.PerformCheckinInteractor;
import com.shootr.android.ui.views.CheckinView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CheckinPresenterTest {

    private static final String EVENT_ID = "event";
    public static final boolean NOT_CHECKED_IN = false;

    @Mock GetCheckinStatusInteractor getCheckinStatusInteractor;
    @Mock PerformCheckinInteractor performCheckinInteractor;
    @Mock CheckinView checkinView;
    @Mock BooleanPreference askCheckinConfirmation;

    private CheckinPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new CheckinPresenter(getCheckinStatusInteractor, performCheckinInteractor, askCheckinConfirmation);
        presenter.setView(checkinView);
        presenter.setEventId(EVENT_ID);
    }

    //region Initialization
    @Test
    public void shouldShowCheckinButtonOnInitializedWhenNotCheckedInEvent() throws Exception {
        setupCheckinStatusCallbacks(false);

        presenter.initialize(checkinView, EVENT_ID);

        verify(checkinView).showCheckinButton();
    }

    @Test
    public void shouldNotShowCheckinButtonOnInitializedWhenCheckedInEvent() throws Exception {
        setupCheckinStatusCallbacks(true);

        presenter.initialize(checkinView, EVENT_ID);

        verify(checkinView, never()).showCheckinButton();
    }
    //endregion

    //region Check in (not checked in already)
    @Test
    public void shouldPerformCheckinWhenCheckinClickedIfAskCheckinConfirmationIsFalse() throws Exception {
        setupCheckinStatusCallbacks(NOT_CHECKED_IN);
        when(askCheckinConfirmation.get()).thenReturn(false);

        presenter.checkIn();

        verify(performCheckinInteractor).performCheckin(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test
    public void shouldNotPerformCheckinWhenCheckinClickedIfAskCheckinConfirmationIsTrue() throws Exception {
        setupCheckinStatusCallbacks(NOT_CHECKED_IN);
        when(askCheckinConfirmation.get()).thenReturn(true);

        presenter.checkIn();

        verify(performCheckinInteractor, never()).performCheckin(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test
    public void shouldShowCheckinConfirmationWhenCheckinClickedIfAskCheckinConfirmationIsTrue() throws Exception {
        setupCheckinStatusCallbacks(NOT_CHECKED_IN);
        when(askCheckinConfirmation.get()).thenReturn(true);

        presenter.checkIn();

        verify(checkinView).showCheckinConfirmation();
    }

    @Test
    public void shouldNotShowCheckinConfirmationWhenCheckinClickedIfAskCheckinConfirmationIsFalse() throws Exception {
        setupCheckinStatusCallbacks(NOT_CHECKED_IN);
        when(askCheckinConfirmation.get()).thenReturn(true);

        presenter.checkIn();

        verify(checkinView, never()).showCheckinConfirmation();
    }

    @Test
    public void shouldPerformCheckinWhenCheckinConfirmed() throws Exception {
        setupCheckinStatusCallbacks(false);

        presenter.confirmCheckin();

        verify(performCheckinInteractor).performCheckin(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test
    public void shouldPerformCheckinWhenCheckinConfirmedDontShowAgain() throws Exception {
        setupCheckinStatusCallbacks(false);

        presenter.confirmCheckinDontShowAgain();

        verify(performCheckinInteractor).performCheckin(anyString(), anyCallback(), anyErrorCallback());
    }

    @Test
    public void shouldSetAskCheckinConfirmationFalseWhenCheckinConfirmedDontShowAgain() throws Exception {
        setupCheckinStatusCallbacks(false);

        presenter.confirmCheckinDontShowAgain();

        verify(askCheckinConfirmation).set(false);
    }

    @Test
    public void shouldNotSetAskCheckinConfirmationWhenCheckinConfirmed() throws Exception {
        setupCheckinStatusCallbacks(false);

        presenter.confirmCheckin();

        verify(askCheckinConfirmation, never()).set(anyBoolean());
    }
    // endregion

    //region Post-Checkin behaviour
    @Test
    public void shouldHideCheckinButtonWhenPerformCheckinCallbacks() throws Exception {
        setupCheckinStatusCallbacks(NOT_CHECKED_IN);
        when(askCheckinConfirmation.get()).thenReturn(false);
        setupPerformCheckinCallbacksCompleted();

        presenter.checkIn();

        verify(checkinView).hideCheckinButton();
    }

    @Test
    public void shouldShowCheckinDoneWhenPerformCheckinCallbacks() throws Exception {
        setupCheckinStatusCallbacks(NOT_CHECKED_IN);
        when(askCheckinConfirmation.get()).thenReturn(false);
        setupPerformCheckinCallbacksCompleted();

        presenter.checkIn();

        verify(checkinView).showCheckinDone();
    }

    @Test
    public void shouldShowCheckinErrorWhenPerformCheckinFails() throws Exception {
        setupCheckinStatusCallbacks(NOT_CHECKED_IN);
        when(askCheckinConfirmation.get()).thenReturn(false);
        setupPerformCheckinCallbacksError();

        presenter.checkIn();

        verify(checkinView).showCheckinError();
    }
    //endregion

    private void setupCheckinStatusCallbacks(final boolean isCheckedIn) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<Boolean> callback = (Interactor.Callback<Boolean>) invocation.getArguments()[1];
                callback.onLoaded(isCheckedIn);
                return null;
            }
        }).when(getCheckinStatusInteractor)
          .loadCheckinStatus(eq(EVENT_ID), any(GetCheckinStatusInteractor.Callback.class));
    }

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