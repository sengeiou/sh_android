package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.PinShotInteractor;
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

public class PinShotPresenterTest {

    public static final String ID_USER = "idUser";

    @Mock PinShotInteractor pinShotInteractor;
    @Mock PinShotView pinShotView;

    private PinShotPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new PinShotPresenter(pinShotInteractor);
        presenter.setView(pinShotView);
    }

    @Test public void shouldNotifyShotPinnedWhenPinShot() throws Exception {
        setupPinShotCallback();

        presenter.pinToProfile(shotModel());

        pinShotView.notifyPinnedShot(any(ShotModel.class));
    }

    @Test public void shouldFeedbackShotPinnedWhenPinShot() throws Exception {
        setupPinShotCallback();

        presenter.pinToProfile(shotModel());

        pinShotView.showPinned();
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

    private ShotModel shotModel() {
        ShotModel shotModel = new ShotModel();
        shotModel.setIdUser(ID_USER);
        shotModel.setHide(1L);
        return shotModel;
    }

}
