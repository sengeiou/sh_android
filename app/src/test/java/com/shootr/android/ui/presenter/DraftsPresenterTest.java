package com.shootr.android.ui.presenter;

import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.views.DraftsView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DraftsPresenterTest {

    private DraftsPresenter presenter;
    @Mock DraftsPresenter.MockDraftsProvider mockDraftsProvider;
    @Mock DraftsView draftsView;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new DraftsPresenter(mockDraftsProvider);
    }

    @Test
    public void shouldShowEmptyViewWhenDraftListIsEmpty() throws Exception {
        when(mockDraftsProvider.getDrafts()).thenReturn(drafts(0));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).showEmpty();
        verify(draftsView, never()).hideEmpty();
    }

    @Test
    public void shouldHideEmptyViewWhenDraftListNotEmpty() throws Exception {
        when(mockDraftsProvider.getDrafts()).thenReturn(drafts(1));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).hideEmpty();
        verify(draftsView, never()).showEmpty();
    }

    @Test
    public void shouldShowDraftsInViewWhenDraftListNotEmpty() throws Exception {
        when(mockDraftsProvider.getDrafts()).thenReturn(drafts(1));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).showDrafts(anyListOf(ShotModel.class));
    }

    @Test
    public void shouldNotShowDraftsInViewWhenDraftListEmpty() throws Exception {
        when(mockDraftsProvider.getDrafts()).thenReturn(drafts(0));

        presenter.initialize(draftsView);

        verify(draftsView, never()).showDrafts(anyListOf(ShotModel.class));
    }

    @Test
    public void shouldHideShootAllButtonWhenDraftListIsEmpty() throws Exception {
        when(mockDraftsProvider.getDrafts()).thenReturn(drafts(0));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).hideShootAllButton();
    }

    @Test
    public void shouldHideShootAllButtonWhenDraftListHasOneItems() throws Exception {
        when(mockDraftsProvider.getDrafts()).thenReturn(drafts(1));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).hideShootAllButton();
    }

    @Test
    public void shouldShowShootAllButtonWhenDraftListHasTwoItems() throws Exception {
        when(mockDraftsProvider.getDrafts()).thenReturn(drafts(2));

        presenter.initialize(draftsView);

        verify(draftsView, times(1)).showShootAllButton();
    }

    private List<ShotModel> drafts(int count) {
        List<ShotModel> shots = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            shots.add(shot());
        }
        return shots;
    }

    private ShotModel shot() {
        ShotModel shotModel = new ShotModel();
        shotModel.setUsername("username");
        shotModel.setComment("comment");
        shotModel.setPhoto("avatar");
        shotModel.setEventTag("tag");
        shotModel.setCsysBirth(new Date());
        return shotModel;
    }
}