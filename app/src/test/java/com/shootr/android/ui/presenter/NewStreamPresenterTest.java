package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.event.CreateEventInteractor;
import com.shootr.android.domain.interactor.event.DeleteEventInteractor;
import com.shootr.android.domain.interactor.event.GetEventInteractor;
import com.shootr.android.ui.model.mappers.EventModelMapper;
import com.shootr.android.ui.views.NewEventView;
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

    @Mock CreateEventInteractor createEventInteractor;
    @Mock GetEventInteractor getEventInteractor;
    @Mock DeleteEventInteractor deleteEventInteractor;
    @Mock EventModelMapper eventModelMapper;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock NewEventView newEventView;

    private NewEventPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new NewEventPresenter(createEventInteractor, getEventInteractor, deleteEventInteractor, eventModelMapper, errorMessageFactory);
    }

    @Test
    public void shouldChangeShortTitleWhenEditTitle() {
        presenter.initialize(newEventView, null);
        presenter.titleTextChanged("Short Title");
        verify(newEventView).showShortTitle(anyString());
    }

    @Test
    public void shouldHaveShortTitleWith15charactersMax() {
        presenter.initialize(newEventView, null);
        presenter.titleTextChanged("Title with more than tewnty and some characters");
        verify(newEventView).showShortTitle("Title with more than");
    }

    @Test
    public void shouldUpdateDoneButtonWhenEditShortTitle() {
        presenter.initialize(newEventView, null);
        presenter.shortTitleTextChanged("Short Title");
        verify(newEventView, atLeastOnce()).doneButtonEnabled(anyBoolean());
    }

    @Test
    public void shouldUpdateDoneButtonStatusWhenEditTitle() {
        presenter.initialize(newEventView, null);
        presenter.titleTextChanged("Title with more than 15 characters");
        verify(newEventView).showShortTitle(anyString());
    }

    @Test
    public void shouldShortTitleBeSameAsTitleWhenTitleEdited() throws Exception {
        presenter.initialize(newEventView, null);
        presenter.titleTextChanged("title");

        verify(newEventView).showShortTitle("title");
    }

    @Test
    public void shouldShortTitleNotBeSameAsTitleWhenTitleEditedAfterShortTitleHasBeenEdited() throws Exception {
        presenter.initialize(newEventView, null);
        presenter.titleTextChanged("old title");
        presenter.shortTitleTextChanged("short title");
        reset(newEventView);

        presenter.titleTextChanged("new title");

        verify(newEventView, never()).showShortTitle(anyString());
    }

}
