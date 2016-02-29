package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.PinShotInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.views.PinShotView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PinShotPresenterTest {

    public static final String ID_USER = "idUser";
    public static final long SHOT_VISIBLE = 0L;
    public static final long SHOT_HIDDEN = 1L;

    @Mock PinShotInteractor pinShotInteractor;
    @Mock PinShotView pinShotView;
    @Mock SessionRepository sessionRepository;

    private PinShotPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new PinShotPresenter(pinShotInteractor, sessionRepository);
        presenter.setView(pinShotView);
    }

    @Test public void shouldNotifyShotPinnedWhenPinShot() throws Exception {
        setupPinShotCallback();

        presenter.pinToProfile(shotModel(SHOT_VISIBLE));

        pinShotView.notifyPinnedShot(any(ShotModel.class));
    }

    @Test public void shouldFeedbackShotPinnedWhenPinShot() throws Exception {
        setupPinShotCallback();

        presenter.pinToProfile(shotModel(SHOT_VISIBLE));

        pinShotView.showPinned();
    }

    @Test public void shouldShowPinButtonIfShotModelIsHiddenWhenInitializedWithShotModel() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);

        presenter.initialize(pinShotView, shotModel(SHOT_HIDDEN));

        verify(pinShotView).showPinShotButton();
    }

    @Test public void shouldHidePinButtonIfShotModelIsHiddenWhenInitializedWithShotModel() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);

        presenter.initialize(pinShotView, shotModel(SHOT_VISIBLE));

        verify(pinShotView).hidePinShotButton();
    }

    @Test public void shouldHidePinButtonIfUserIdInSessionIsNull() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(null);

        presenter.initialize(pinShotView, shotModel(SHOT_VISIBLE));

        verify(pinShotView).hidePinShotButton();
    }

    public void setupPinShotCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(pinShotInteractor).pinShot(anyString(), any(Interactor.CompletedCallback.class));
    }

    private ShotModel shotModel(Long hidden) {
        ShotModel shotModel = new ShotModel();
        shotModel.setIdUser(ID_USER);
        shotModel.setHide(hidden);
        return shotModel;
    }

}
