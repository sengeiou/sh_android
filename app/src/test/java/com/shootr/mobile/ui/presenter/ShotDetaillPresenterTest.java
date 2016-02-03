package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.ShotDetail;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.GetShotDetailInteractor;
import com.shootr.mobile.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.mobile.domain.interactor.shot.ShareShotInteractor;
import com.shootr.mobile.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.mobile.ui.model.ShotModel;
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
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ShotDetaillPresenterTest {

    private static final String ID_SHOT = "idShot";
    private ShotDetailPresenter presenter;
    @Mock GetShotDetailInteractor getShotDetaillInteractor;
    @Mock MarkNiceShotInteractor markNiceShotInteractor;
    @Mock UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    @Mock ShareShotInteractor shareShotInteractor;
    @Mock Bus bus;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock ShotDetailView shotDetailView;
    @Mock List<ShotModel> repliesModels;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShotModelMapper shotModelMapper = new ShotModelMapper();
        presenter = new ShotDetailPresenter(getShotDetaillInteractor,
          markNiceShotInteractor,
          unmarkNiceShotInteractor,
          shareShotInteractor,
          shotModelMapper,
          bus,
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

        verify(shotDetailView).renderParent(any(ShotModel.class));
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

        verify(shotDetailView).renderParent(any(ShotModel.class));
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

        presenter.initialize(shotDetailView,shotModel());
        presenter.markNiceShot(ID_SHOT);

        verify(shotDetailView,times(2)).renderReplies(anyListOf(ShotModel.class));
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

        presenter.initialize(shotDetailView,shotModel());
        presenter.unmarkNiceShot(ID_SHOT);

        verify(shotDetailView,times(2)).renderReplies(anyListOf(ShotModel.class));
    }

    private List<Shot> shotList(int shots){
        ArrayList<Shot> shotList = new ArrayList<>();
        for(int i=0;i<shots;i++){
            shotList.add(shot());
        }
        return shotList;
    }

    private Shot shot(){
        Shot shot = new Shot();
        shot.setIdShot(ID_SHOT);
        shot.setStreamInfo(new Shot.ShotStreamInfo());
        shot.setUserInfo(new Shot.ShotUserInfo());
        return shot;
    }

    private ShotModel shotModel(){
        ShotModel shotModel = new ShotModel();
        shotModel.setIdShot(ID_SHOT);
        return shotModel;
    }

    private ShotDetail shotDetail() {
        ShotDetail shotDetail= new ShotDetail();
        shotDetail.setShot(shot());
        shotDetail.setReplies(Collections.<Shot>emptyList());
        return shotDetail;
    }

    private ShotDetail shotDetailWithReplies() {
        ShotDetail shotDetail= new ShotDetail();
        shotDetail.setShot(shot());
        shotDetail.setReplies(shotList(2));
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
        }).when(unmarkNiceShotInteractor).unmarkNiceShot(anyString(), any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupGetShotDetailInteractorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<ShotDetail> callback =
                  (Interactor.Callback<ShotDetail>) invocation.getArguments()[1];
                callback.onLoaded(shotDetail());
                return null;
            }
        }).when(getShotDetaillInteractor).loadShotDetail(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupGetShotDetailWithRepliesInteractorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<ShotDetail> callback =
                  (Interactor.Callback<ShotDetail>) invocation.getArguments()[1];
                callback.onLoaded(shotDetailWithReplies());
                return null;
            }
        }).when(getShotDetaillInteractor).loadShotDetail(anyString(), any(Interactor.Callback.class),
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
        }).when(markNiceShotInteractor).markNiceShot(anyString(),
          any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }
}