package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.shot.DeleteDraftInteractor;
import com.shootr.mobile.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.mobile.domain.interactor.shot.SendDraftInteractor;
import com.shootr.mobile.domain.interactor.timeline.privateMessage.SendPrivateMessageDraftInteractor;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.DraftModel;
import com.shootr.mobile.ui.model.mappers.DraftModelMapper;
import com.shootr.mobile.ui.model.mappers.PrivateMessageModelMapper;
import com.shootr.mobile.ui.model.mappers.ShotModelMapper;
import com.shootr.mobile.ui.views.DraftsView;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DraftsPresenterTest {

    private DraftsPresenter presenter;
    @Mock DraftsView draftsView;
    @Mock GetDraftsInteractor interactor;
    @Mock SendDraftInteractor sendDraftInteractor;
    @Mock DeleteDraftInteractor deleteDraftInteractor;
    @Mock SendPrivateMessageDraftInteractor sendPrivateMessageDraftInteractor;
    @Mock Bus bus;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        SessionRepository sessionRepository = mock(SessionRepository.class);
        when(sessionRepository.getCurrentUser()).thenReturn(currentUser());
        DraftModelMapper draftModelMapper = new DraftModelMapper(sessionRepository, new ShotModelMapper(),
            new PrivateMessageModelMapper(sessionRepository));
        presenter = new DraftsPresenter(interactor, sendDraftInteractor,
            sendPrivateMessageDraftInteractor, deleteDraftInteractor, draftModelMapper, bus);
    }

    @Test public void shouldShowEmptyViewWhenDraftListIsEmpty() throws Exception {
        setupInteractorReturns(drafts(0));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).showEmpty();
        verify(draftsView, never()).hideEmpty();
    }

    @Test public void shouldHideEmptyViewWhenDraftListNotEmpty() throws Exception {
        setupInteractorReturns(drafts(1));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).hideEmpty();
        verify(draftsView, never()).showEmpty();
    }

    @Test public void shouldShowDraftsInViewWhenDraftListNotEmpty() throws Exception {
        setupInteractorReturns(drafts(1));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).showDrafts(anyListOf(DraftModel.class));
    }

    @Test public void shouldShowDraftsInViewWhenDraftListEmpty() throws Exception {
        setupInteractorReturns(drafts(0));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).showDrafts(anyListOf(DraftModel.class));
    }

    @Test public void shouldHideShootAllButtonWhenDraftListIsEmpty() throws Exception {
        setupInteractorReturns(drafts(0));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).hideShootAllButton();
    }

    @Test public void shouldHideShootAllButtonWhenDraftListHasOneItems() throws Exception {
        setupInteractorReturns(drafts(1));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).hideShootAllButton();
    }

    @Test public void shouldShowShootAllButtonWhenDraftListHasTwoItems() throws Exception {
        setupInteractorReturns(drafts(2));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).showShootAllButton();
    }

    private void setupInteractorReturns(final List<QueuedShot> drafts) {
        doAnswer(new Answer<Void>() {
            @Override public Void answer(InvocationOnMock invocation) throws Throwable {
                GetDraftsInteractor.Callback callback = (GetDraftsInteractor.Callback) invocation.getArguments()[0];
                callback.onLoaded(drafts);
                return null;
            }
        }).when(interactor).loadDrafts(any(GetDraftsInteractor.Callback.class));
    }

    private List<QueuedShot> drafts(int count) {
        List<QueuedShot> shots = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            shots.add(draft());
        }
        return shots;
    }

    private QueuedShot draft() {
        QueuedShot draft = new QueuedShot();
        draft.setBaseMessage(shot());
        return draft;
    }

    private Shot shot() {
        Shot shot = new Shot();
        shot.setComment("comment");
        shot.setUserInfo(new BaseMessage.BaseMessageUserInfo());
        return shot;
    }

    private User currentUser() {
        User user = new User();
        return user;
    }
}