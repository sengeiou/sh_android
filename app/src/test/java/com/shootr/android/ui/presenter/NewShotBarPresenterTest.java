package com.shootr.android.ui.presenter;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.interactor.shot.GetDraftsInteractor;
import com.shootr.android.ui.views.NewShotBarView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class NewShotBarPresenterTest {

    @Mock GetDraftsInteractor getDraftsInteractor;
    @Mock NewShotBarView newShotBarView;

    private NewShotBarPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new NewShotBarPresenter(getDraftsInteractor);
        presenter.setView(newShotBarView);
    }

    @Test
    public void shouldCheckDraftsWhenInitialized() throws Exception {
        presenter.initialize(newShotBarView);

        verify(getDraftsInteractor).loadDrafts(any(GetDraftsInteractor.Callback.class));
    }

    @Test
    public void shouldShowDraftsButtonWhenGetDraftsReturnsDrafts() throws Exception {
        setupDraftsInteractorCallbacks(draftsList());

        presenter.initialize(newShotBarView);

        verify(newShotBarView).showDraftsButton();
    }

    @Test
    public void shouldHideDraftsButtonWhenGetDraftsReturnsEmpty() throws Exception {
        setupDraftsInteractorCallbacks(emptyDraftsList());

        presenter.initialize(newShotBarView);

        verify(newShotBarView).hideDraftsButton();
    }

    private List<QueuedShot> draftsList() {
        return Arrays.asList(queuedShot());
    }

    private List<QueuedShot> emptyDraftsList() {
        return new ArrayList<>();
    }

    private QueuedShot queuedShot() {
        return new QueuedShot();
    }

    private void setupDraftsInteractorCallbacks(final List<QueuedShot> drafts) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((GetDraftsInteractor.Callback) invocation.getArguments()[0]).onLoaded(drafts);
                return null;
            }
        }).when(getDraftsInteractor).loadDrafts(any(GetDraftsInteractor.Callback.class));
    }
}