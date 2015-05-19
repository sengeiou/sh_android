package com.shootr.android.ui.presenter;

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
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CheckinPresenterTest {

    private static final String EVENT_ID = "event";

    @Mock GetCheckinStatusInteractor getCheckinStatusInteractor;
    @Mock PerformCheckinInteractor performCheckinInteractor;
    @Mock CheckinView checkinView;

    private CheckinPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new CheckinPresenter(getCheckinStatusInteractor, performCheckinInteractor);
        presenter.setView(checkinView);
    }

    //region Initialization
    @Test public void shouldShowCheckinButtonOnInitializedWhenNotCheckedInEvent() throws Exception {
        setupCheckinStatusCallbacks(false);

        presenter.initialize(checkinView, EVENT_ID);

        verify(checkinView).showCheckinButton();
    }

    @Test public void shouldNotShowCheckinButtonOnInitializedWhenCheckedInEvent() throws Exception {
        setupCheckinStatusCallbacks(true);

        presenter.initialize(checkinView, EVENT_ID);

        verify(checkinView, never()).showCheckinButton();
    }

    //endregion

    //region Check in
    @Test public void shouldPerformCheckinWhenCheckinButtonClickedIfNotCheckedInEvent() throws Exception {
        setupCheckinStatusCallbacks(false);

        presenter.initialize(checkinView, EVENT_ID);
        presenter.checkinClick();

        verify(performCheckinInteractor).performCheckin(anyString(), any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldShowCheckinLoadingWhenCheckIn() throws Exception {
        doNothing().when(performCheckinInteractor)
          .performCheckin(anyString(), any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));

        presenter.checkIn();

        verify(checkinView).showCheckinLoading();
    }

    @Test public void shouldHideCheckinLoadingWhenCheckInCallbacksCompleted() throws Exception {
        setupPerformCheckinCallbacksCompleted();

        presenter.checkIn();

        verify(checkinView).hideCheckinLoading();
    }

    @Test public void shouldBeCheckedInEventWhenPerformCheckinCallbacks() throws Exception {
        setupPerformCheckinCallbacksCompleted();

        presenter.checkIn();

        assertThat(presenter.checkedInEvent).isTrue();
    }
    // endregion

    //region Toolbar interaction

    @Test public void shouldNotShowCheckedInStatusWhenToolbarClickedAfterInteractorCallbacksIfButtonIsNotVisible()
      throws Exception {
        setupCheckinStatusCallbacks(true);
        presenter.initialize(checkinView, EVENT_ID);
        presenter.showingCheckinButton = false;

        presenter.toolbarClick();

        verify(checkinView, never()).showCheckinButton();
    }

    @Test public void shouldHideCheckinButtonWhenToolbarClickedIfButtonIsAlreadyVisible() throws Exception {
        setupCheckinStatusCallbacks(true);
        presenter.initialize(checkinView, EVENT_ID);
        presenter.showingCheckinButton = true;

        presenter.toolbarClick();

        verify(checkinView).hideCheckinButton();
    }

    @Test public void shouldShowCheckedInWhenToolbarClickedIfButtonIsAlreadyVisible() throws Exception {
        setupCheckinStatusCallbacks(true);
        presenter.initialize(checkinView, EVENT_ID);
        presenter.showingCheckinButton = true;

        presenter.toolbarClick();

        verify(checkinView, times(2)).showCheckedIn();
    }

    //endregion

    @Test public void shouldNotInteractWithViewWhenToolbarClickedIfCheckinStatusHasntCallbackYet() throws Exception {
        doNothing().when(getCheckinStatusInteractor).loadCheckinStatus(anyString(), any(Interactor.Callback.class));

        presenter.toolbarClick();

        verifyZeroInteractions(checkinView);
    }

    private void setupCheckinStatusCallbacks(final boolean isCheckedIn) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<Boolean> callback = (Interactor.Callback<Boolean>) invocation.getArguments()[1];
                callback.onLoaded(isCheckedIn);
                return null;
            }
        }).when(getCheckinStatusInteractor)
          .loadCheckinStatus(eq(EVENT_ID), any(GetCheckinStatusInteractor.Callback.class));
    }

    private void setupPerformCheckinCallbacksCompleted() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback = (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(performCheckinInteractor).performCheckin(anyString(),
          any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }
}