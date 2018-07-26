package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.CallCtaCheckInInteractor;
import com.shootr.mobile.domain.interactor.shot.ClickShotLinkEventInteractor;
import com.shootr.mobile.domain.interactor.shot.GetShotDetailInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UndoReshootInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ViewShotDetailEventInteractor;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.EntitiesModelMapper;
import com.shootr.mobile.ui.model.mappers.NicerModelMapper;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.views.ShotDetailView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ShotDetaillPresenterTest {

    private static final String ID_SHOT = "idShot";
    private static final String ID_USER = "idUser";
    private static final String OTHER_STREAM_ID = "otherStreamId";
    private static final String STREAM_ID = "streamId";
    private static final Boolean IS_NOT_IN_STREAM_TIMELINE = false;
    private static final Boolean IS_IN_STREAM_TIMELINE = true;
    private ShotDetailPresenter presenter;
    @Mock GetShotDetailInteractor getShotDetaillInteractor;
    @Mock MarkNiceShotInteractor markNiceShotInteractor;
    @Mock UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    @Mock ReshootInteractor reshootInteractor;
    @Mock UndoReshootInteractor undoReshootInteractor;
    @Mock ViewShotDetailEventInteractor viewShotEventInteractor;
    @Mock ClickShotLinkEventInteractor clickShotLinkEventInteractor;
    @Mock CallCtaCheckInInteractor callCtaCheckInInteractor;
    @Mock Bus bus;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock ShotDetailView shotDetailView;
    @Mock List<ShotModel> repliesModels;
    @Mock SessionRepository sessionRepository;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShotModelMapper shotModelMapper = new ShotModelMapper(sessionRepository,
            new EntitiesModelMapper(sessionRepository));
        NicerModelMapper nicerModelMapper = new NicerModelMapper();
        presenter = new ShotDetailPresenter(getShotDetaillInteractor,
          markNiceShotInteractor,
          unmarkNiceShotInteractor, reshootInteractor, undoReshootInteractor,
            viewShotEventInteractor, clickShotLinkEventInteractor,
            callCtaCheckInInteractor, shotModelMapper, nicerModelMapper, bus,
          errorMessageFactory);
        presenter.setShotDetailView(shotDetailView);
    }

    @Test public void shouldRenderShotWhenNiceIsMarked() throws Exception {
        presenter.setNiceBlocked(false);
        presenter.setShotModel(shotModel());
        setupMarkNiceShotInteractorCallback();
        setupGetShotDetailInteractorCallback();

        presenter.markNiceShot(ID_SHOT);

        verify(shotDetailView).renderShot(any(ShotModel.class));
    }

    @Test public void shouldRenderParentWhenNiceParentIsMarked() throws Exception {
        presenter.setNiceBlocked(false);
        presenter.setShotModel(shotModel());
        setupMarkNiceShotInteractorCallback();
        setupGetShotDetailInteractorCallback();

        presenter.markNiceShot(ID_SHOT);

        verify(shotDetailView).renderParents(anyList());
    }

    @Test public void shouldSetReplyUsernameWhenNiceIsMarked() throws Exception {
        presenter.setNiceBlocked(false);
        presenter.setShotModel(shotModel());
        setupMarkNiceShotInteractorCallback();
        setupGetShotDetailInteractorCallback();

        presenter.markNiceShot(ID_SHOT);

        verify(shotDetailView).setReplyUsername(anyString());
    }

    @Test public void shouldRenderShotWhenNiceIsUnmarked() throws Exception {
        presenter.setNiceBlocked(false);
        presenter.setShotModel(shotModel());
        setupUnmarkNiceShotInteractorCallback();
        setupGetShotDetailInteractorCallback();

        presenter.unmarkNiceShot(ID_SHOT);

        verify(shotDetailView).renderShot(any(ShotModel.class));
    }

    @Test public void shouldRenderParentWhenNiceIsUnmarked() throws Exception {
        presenter.setNiceBlocked(false);
        presenter.setShotModel(shotModel());
        setupUnmarkNiceShotInteractorCallback();
        setupGetShotDetailInteractorCallback();

        presenter.unmarkNiceShot(ID_SHOT);

        verify(shotDetailView).renderParents(anyList());
    }

    @Test public void shouldSetReplyUsernameWhenNiceIsUnmarked() throws Exception {
        presenter.setNiceBlocked(false);
        presenter.setShotModel(shotModel());
        setupUnmarkNiceShotInteractorCallback();
        setupGetShotDetailInteractorCallback();

        presenter.unmarkNiceShot(ID_SHOT);

        verify(shotDetailView).setReplyUsername(anyString());
    }

    @Test public void shouldRenderRepliesWhenNiceIsMarked() throws Exception {
        presenter.setNiceBlocked(false);
        presenter.setShotModel(shotModel());
        setupMarkNiceShotInteractorCallback();
        setupGetShotDetailInteractorCallback();

        presenter.markNiceShot(ID_SHOT);

        verify(shotDetailView).renderReplies(anyList());
    }

    @Test public void shouldRenderRepliesWhenNiceIsUnmarked() throws Exception {
        presenter.setNiceBlocked(false);
        presenter.setShotModel(shotModel());
        setupUnmarkNiceShotInteractorCallback();
        setupGetShotDetailInteractorCallback();

        presenter.unmarkNiceShot(ID_SHOT);

        verify(shotDetailView).renderReplies(anyList());
    }

    @Test public void shouldRenderRepliesWhenNewRepliesAreZeroAndIsMarked() throws Exception {
        setupGetShotDetailInteractorCallback();
        setupMarkNiceShotInteractorCallback();

        presenter.initialize(shotDetailView, shotModel());
        presenter.markNiceShot(ID_SHOT);

        verify(shotDetailView, times(2)).renderReplies(anyListOf(ShotModel.class));
    }

    @Test public void shouldRenderRepliesWhenNewRepliesAreEqualsThanPreviousAndIsMarked() throws Exception {
        setupGetShotDetailWithRepliesInteractorCallback();
        setupMarkNiceShotInteractorCallback();

        presenter.initialize(shotDetailView, shotModel());
        presenter.markNiceShot(ID_SHOT);

        verify(shotDetailView, times(2)).renderReplies(anyListOf(ShotModel.class));
    }

    @Test public void shouldRenderParentsWhenNewParentsAreZeroAndIsMarked() throws Exception {
        setupGetShotDetailInteractorCallback();
        setupMarkNiceShotInteractorCallback();

        presenter.initialize(shotDetailView, shotModel());
        presenter.markNiceShot(ID_SHOT);

        verify(shotDetailView, times(2)).renderParents(anyListOf(ShotModel.class));
    }

    @Test public void shouldRenderParentsWhenNewParentsAreEqualsThanPreviousAndIsMarked() throws Exception {
        setupGetShotDetailWithRepliesInteractorCallback();
        setupMarkNiceShotInteractorCallback();

        presenter.initialize(shotDetailView, shotModel());
        presenter.markNiceShot(ID_SHOT);

        verify(shotDetailView, times(2)).renderParents(anyListOf(ShotModel.class));
    }

    @Test public void shouldRenderRepliesWhenNewRepliesAreZeroAndIsUnmarked() throws Exception {
        setupGetShotDetailInteractorCallback();
        setupUnmarkNiceShotInteractorCallback();

        presenter.initialize(shotDetailView, shotModel());
        presenter.unmarkNiceShot(ID_SHOT);

        verify(shotDetailView, times(2)).renderReplies(anyListOf(ShotModel.class));
    }

    @Test public void shouldRenderRepliesWhenNewRepliesAreEqualsThanPreviousAndIsUnmarked() throws Exception {
        setupGetShotDetailWithRepliesInteractorCallback();
        setupUnmarkNiceShotInteractorCallback();

        presenter.initialize(shotDetailView, shotModel());
        presenter.unmarkNiceShot(ID_SHOT);

        verify(shotDetailView, times(2)).renderReplies(anyListOf(ShotModel.class));
    }

    @Test public void shouldLoadShotDetailFromShotIdWhenInitializedFromDeepLinking() throws Exception {
        setupGetShotDetailInteractorCallback();

        presenter.initialize(shotDetailView, ID_SHOT);

        verify(getShotDetaillInteractor).loadShotDetail(anyString(), anyBoolean(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldSetupNewShotBarDelegateWhenInitializedFromDeepLinking() throws Exception {
        setupGetShotDetailInteractorCallback();

        presenter.initialize(shotDetailView, ID_SHOT);

        verify(shotDetailView).setupNewShotBarDelegate(any(ShotModel.class));
    }

    @Test public void shouldInitializeNewShotBarDelegateWhenInitializedFromDeepLinking() throws Exception {
        setupGetShotDetailInteractorCallback();

        presenter.initialize(shotDetailView, ID_SHOT);

        verify(shotDetailView).initializeNewShotBarPresenter(anyString());
    }

    @Test public void shouldLoadShotDetailFromShotModelWhenShotModelIsNotNull() throws Exception {
        setupGetShotDetailInteractorCallback();

        presenter.initialize(shotDetailView, shotModel());

        verify(getShotDetaillInteractor).loadShotDetail(anyString(), anyBoolean(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldNotLoadShotDetailFromShotModelWhenShotModelIsNull() throws Exception {
        setupGetShotDetailInteractorCallback();

        presenter.initialize(shotDetailView, shotModelNull());

        verify(getShotDetaillInteractor, never()).loadShotDetail(anyString(), anyBoolean(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldShowErrorWhenLoadShotDetailANdShotModelIsNull() throws Exception {
        setupGetShotDetailInteractorCallback();

        presenter.initialize(shotDetailView, shotModelNull());

        verify(shotDetailView).showError(anyString());
    }

    @Test public void shouldOpenShotWhenParentOrReplyShotClick() throws Exception {
        presenter.initialize(shotDetailView, shotModelNull());

        presenter.shotClick(shotModel());

        verify(shotDetailView).openShot(any(ShotModel.class));
    }

    @Test public void shouldGoToStreamTimelineWhenNotConnectedInShotModelStream() throws Exception {
        presenter.streamTitleClick(shotModel());

        verify(shotDetailView).goToStreamTimeline(anyString());
    }

    @Test public void shouldEnableStreamTitleWhenIsNotInShotStream() throws Exception {
        presenter.initialize(shotDetailView, shotModel());

        presenter.setupStreamTitle(IS_NOT_IN_STREAM_TIMELINE);

        verify(shotDetailView).enableStreamTitle();
    }

    @Test public void shouldDisableStreamTitleWhenIsInShotStream() throws Exception {
        ShotModel shotModel = shotModel();
        shotModel.setStreamId(OTHER_STREAM_ID);

        presenter.initialize(shotDetailView, shotModel);

        presenter.setupStreamTitle(IS_IN_STREAM_TIMELINE);

        verify(shotDetailView).disableStreamTitle();
    }

    @Test public void shouldShowShotSharedWhenInitializeWithIdShotAndShareShotViaShootr() throws Exception {
        setupShareShotInteractor();
        setupGetShotDetailInteractorCallback();
        presenter.initialize(shotDetailView, ID_SHOT);

        presenter.reshoot();

        verify(shotDetailView).showReshoot(anyBoolean());
    }

    @Test public void shouldShowShotSharedWhenInitializeWithShotModelAndShareShotViaShootr() throws Exception {
        setupShareShotInteractor();
        setupGetShotDetailInteractorCallback();
        presenter.initialize(shotDetailView, shotModel());

        presenter.reshoot();

        verify(shotDetailView).showReshoot(anyBoolean());
    }

    @Test public void shouldShareShotWhenInitializeWithShotModel() throws Exception {
        setupShareShotInteractor();
        setupGetShotDetailInteractorCallback();
        presenter.initialize(shotDetailView, shotModel());

        presenter.shareShot();

        verify(shotDetailView).shareShot(any(ShotModel.class));
    }

    @Test public void shouldShareShotWhenInitializeWithIdShot() throws Exception {
        setupShareShotInteractor();
        setupGetShotDetailInteractorCallback();
        presenter.initialize(shotDetailView, ID_SHOT);

        presenter.shareShot();

        verify(shotDetailView).shareShot(any(ShotModel.class));
    }

    private List<Shot> shotList(int shots) {
        ArrayList<Shot> shotList = new ArrayList<>();
        for (int i = 0; i < shots; i++) {
            shotList.add(shot());
        }
        return shotList;
    }

    private Shot shot() {
        Shot shot = new Shot();
        shot.setIdShot(ID_SHOT);
        shot.setStreamInfo(new Shot.ShotStreamInfo());
        shot.setUserInfo(new BaseMessage.BaseMessageUserInfo());
        shot.setReshooted(false);
        shot.setNiced(false);
        return shot;
    }

    private ShotModel shotModel() {
        ShotModel shotModel = new ShotModel();
        shotModel.setIdShot(ID_SHOT);
        shotModel.setStreamId(STREAM_ID);
        return shotModel;
    }

    private ShotModel shotModelNull() {
        return null;
    }

    private ShotDetail shotDetail() {
        ShotDetail shotDetail = new ShotDetail();
        shotDetail.setShot(shot());
        shotDetail.setReplies(Collections.<Shot>emptyList());
        shotDetail.setParents(shotList(2));
        return shotDetail;
    }

    private ShotDetail shotDetailWithReplies() {
        ShotDetail shotDetail = new ShotDetail();
        shotDetail.setShot(shot());
        shotDetail.setReplies(shotList(2));
        shotDetail.setParents(Collections.<Shot>emptyList());
        return shotDetail;
    }

    private void setupUnmarkNiceShotInteractorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(unmarkNiceShotInteractor)
          .unmarkNiceShot(anyString(),
            any(Interactor.CompletedCallback.class),
            any(Interactor.ErrorCallback.class));
    }

    private void setupGetShotDetailInteractorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<ShotDetail> callback =
                    (Interactor.Callback<ShotDetail>) invocation.getArguments()[2];
                callback.onLoaded(shotDetail());
                return null;
            }
        }).when(getShotDetaillInteractor)
            .loadShotDetail(anyString(), anyBoolean(), any(Interactor.Callback.class),
                any(Interactor.ErrorCallback.class));
    }

    private void setupGetShotDetailWithRepliesInteractorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<ShotDetail> callback =
                    (Interactor.Callback<ShotDetail>) invocation.getArguments()[2];
                callback.onLoaded(shotDetailWithReplies());
                return null;
            }
        }).when(getShotDetaillInteractor)
            .loadShotDetail(anyString(), anyBoolean(), any(Interactor.Callback.class),
                any(Interactor.ErrorCallback.class));
    }

    private void setupMarkNiceShotInteractorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(markNiceShotInteractor)
          .markNiceShot(anyString(), any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupShareShotInteractor() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(reshootInteractor)
          .reshoot(anyString(), any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }
}
