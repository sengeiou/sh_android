package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.GetShotDetailInteractor;
import com.shootr.mobile.domain.interactor.shot.PinShotInteractor;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.views.PinShotView;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PinShotPresenterTest {

    public static final String ID_USER = "idUser";
    public static final long SHOT_HIDDEN = 1L;
    public static final String ID_SHOT = "idShot";

    @Mock PinShotInteractor pinShotInteractor;
    @Mock PinShotView pinShotView;
    @Mock SessionRepository sessionRepository;
    @Mock GetShotDetailInteractor getShotDetailInteractor;

    private ShotModelMapper shotModelMapper;
    private PinShotPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shotModelMapper = new ShotModelMapper(sessionRepository);
        presenter =
          new PinShotPresenter(pinShotInteractor, getShotDetailInteractor, sessionRepository, shotModelMapper);
        presenter.setView(pinShotView);
    }

    @Test public void shouldNotifyShotPinnedWhenPinShot() throws Exception {
        setupPinShotCallback();

        presenter.pinToProfile(shotModel(null));

        pinShotView.notifyPinnedShot(any(ShotModel.class));
    }

    @Test public void shouldFeedbackShotPinnedWhenPinShot() throws Exception {
        setupPinShotCallback();

        presenter.pinToProfile(shotModel(null));

        pinShotView.showPinned();
    }

    @Test public void shouldShowPinButtonIfShotModelIsHiddenWhenInitializedWithShotModel() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);

        presenter.initialize(pinShotView, shotModel(SHOT_HIDDEN));

        verify(pinShotView).showPinShotButton();
    }

    @Test public void shouldHidePinButtonIfShotModelIsHiddenWhenInitializedWithShotModel() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);

        presenter.initialize(pinShotView, shotModel(null));

        verify(pinShotView).hidePinShotButton();
    }

    @Test public void shouldHidePinButtonIfUserIdInSessionIsNull() throws Exception {
        when(sessionRepository.getCurrentUserId()).thenReturn(null);

        presenter.initialize(pinShotView, shotModel(null));

        verify(pinShotView).hidePinShotButton();
    }

    @Test public void shouldGetShotDetailWhenResumeIfHasBeenPaused() throws Exception {
        presenter.setShot(shotModel(null));

        presenter.pause();
        presenter.resume();

        verify(getShotDetailInteractor).loadShotDetail(anyString(), anyBoolean(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldNotGetShotDetailWhenResumeIfHasNotBeenPaused() throws Exception {
        presenter.resume();

        verify(getShotDetailInteractor, never()).loadShotDetail(anyString(), anyBoolean(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldShowPinButtonIfShotModelIsHiddenWhenResumed() throws Exception {
        setupGetShotDetailInteractorCallback(SHOT_HIDDEN);
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        presenter.setShot(shotModel(SHOT_HIDDEN));

        presenter.pause();
        presenter.resume();

        verify(pinShotView).showPinShotButton();
    }

    @Test public void shouldHidePinButtonIfShotModelIsVisibleWhenResumed() throws Exception {
        setupGetShotDetailInteractorCallback(null);
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        presenter.setShot(shotModel(null));

        presenter.pause();
        presenter.resume();

        verify(pinShotView).hidePinShotButton();
    }

    @Test public void shouldNotShowPinShotButtonWhenCallResumeAndShotModelIsNull() throws Exception {
        presenter.pause();
        presenter.setShot(null);

        presenter.resume();

        verify(pinShotView, never()).showPinShotButton();
    }

    @Test public void shouldNotHidePinShotButtonWhenCallResumeAndShotModelIsNull() throws Exception {
        presenter.pause();
        presenter.setShot(null);

        presenter.resume();

        verify(pinShotView, never()).hidePinShotButton();
    }

    public void setupPinShotCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback = (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(pinShotInteractor).pinShot(anyString(), any(Interactor.CompletedCallback.class));
    }

    private void setupGetShotDetailInteractorCallback(final Long profileHidden) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<ShotDetail> callback =
                    (Interactor.Callback<ShotDetail>) invocation.getArguments()[2];
                callback.onLoaded(shotDetail(profileHidden));
                return null;
            }
        }).when(getShotDetailInteractor)
            .loadShotDetail(anyString(), anyBoolean(), any(Interactor.Callback.class),
                any(Interactor.ErrorCallback.class));
    }

    private ShotModel shotModel(Long hidden) {
        ShotModel shotModel = new ShotModel();
        shotModel.setIdShot(ID_SHOT);
        shotModel.setIdUser(ID_USER);
        shotModel.setHide(hidden);
        return shotModel;
    }

    private ShotDetail shotDetail(Long profileHidden) {
        ShotDetail shotDetail = new ShotDetail();
        shotDetail.setShot(shot(profileHidden));
        shotDetail.setReplies(Collections.<Shot>emptyList());
        return shotDetail;
    }

    private Shot shot(Long profileHidden) {
        Shot shot = new Shot();
        shot.setIdShot(ID_SHOT);
        shot.setProfileHidden(profileHidden);
        shot.setStreamInfo(new Shot.ShotStreamInfo());
        BaseMessage.BaseMessageUserInfo baseMessageUserInfo = new BaseMessage.BaseMessageUserInfo();
        baseMessageUserInfo.setIdUser(ID_USER);
        shot.setUserInfo(baseMessageUserInfo);
        return shot;
    }
}
