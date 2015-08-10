package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.stream.CreateStreamInteractor;
import com.shootr.android.domain.interactor.stream.DeleteStreamInteractor;
import com.shootr.android.domain.interactor.stream.GetStreamInteractor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.ui.model.mappers.StreamModelMapper;
import com.shootr.android.ui.views.NewStreamView;
import com.shootr.android.util.ErrorMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

public class NewStreamPresenterTest {

    @Mock CreateStreamInteractor createStreamInteractor;
    @Mock GetStreamInteractor getStreamInteractor;
    @Mock DeleteStreamInteractor deleteStreamInteractor;
    @Mock SelectStreamInteractor selectStreamInteractor;
    @Mock StreamModelMapper streamModelMapper;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock NewStreamView newStreamView;

    private NewStreamPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new NewStreamPresenter(createStreamInteractor, getStreamInteractor,
          deleteStreamInteractor, selectStreamInteractor, streamModelMapper, errorMessageFactory);
    }

    @Test
    public void shouldChangeShortTitleWhenEditTitle() {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged("Short Title");
        verify(newStreamView).showShortTitle(anyString());
    }

    @Test
    public void shouldHaveShortTitleWith15charactersMax() {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged("Title with more than tewnty and some characters");
        verify(newStreamView).showShortTitle("Title with more than");
    }

    @Test
    public void shouldUpdateDoneButtonWhenEditShortTitle() {
        presenter.initialize(newStreamView, null);
        presenter.shortTitleTextChanged("Short Title");
        verify(newStreamView, atLeastOnce()).doneButtonEnabled(anyBoolean());
    }

    @Test
    public void shouldUpdateDoneButtonStatusWhenEditTitle() {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged("Title with more than 15 characters");
        verify(newStreamView).showShortTitle(anyString());
    }

    @Test
    public void shouldShortTitleBeSameAsTitleWhenTitleEdited() throws Exception {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged("title");

        verify(newStreamView).showShortTitle("title");
    }

    @Test
    public void shouldShortTitleNotBeSameAsTitleWhenTitleEditedAfterShortTitleHasBeenEdited() throws Exception {
        presenter.initialize(newStreamView, null);
        presenter.titleTextChanged("old title");
        presenter.shortTitleTextChanged("short title");
        reset(newStreamView);

        presenter.titleTextChanged("new title");

        verify(newStreamView, never()).showShortTitle(anyString());
    }

}
