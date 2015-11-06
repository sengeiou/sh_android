package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.Stream;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.GetBlogInteractor;
import com.shootr.mobile.domain.interactor.stream.GetHelpInteractor;
import com.shootr.mobile.ui.views.SupportView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class SupportPresenterTest {

    public static final String ID_STREAM = "idStream";
    public static final String TITLE = "title";
    public static final String ID_AUTHOR = "id_author";
    @Mock GetBlogInteractor getBlogInteractor;
    @Mock GetHelpInteractor getHelpInteractor;
    @Mock SupportView supportView;

    private SupportPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new SupportPresenter(getBlogInteractor, getHelpInteractor);
    }

    @Test public void shouldLoadBlogWhenPresenterInitialized() throws Exception {
        presenter.initialize(supportView);

        verify(getBlogInteractor).obtainBlogStream(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldLoadHelpWhenPresenterInitialized() throws Exception {
        presenter.initialize(supportView);

        verify(getHelpInteractor).obtainHelpStream(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    @Test public void shouldGoToBlogStreamIfBlogClickedAndBlogHasBeenLoaded() throws Exception {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<Stream> callback =
                  (Interactor.Callback<Stream>) invocation.getArguments()[0];
                callback.onLoaded(stream());
                return null;
            }
        }).when(getBlogInteractor).obtainBlogStream(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));

        presenter.initialize(supportView);
        presenter.blogClicked();

        verify(supportView).goToStream(stream());
    }

    @Test public void shouldGoToHelpStreamIfHelpClickedAndHelpHasBeenLoaded() throws Exception {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<Stream> callback =
                  (Interactor.Callback<Stream>) invocation.getArguments()[0];
                callback.onLoaded(stream());
                return null;
            }
        }).when(getHelpInteractor).obtainHelpStream(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));

        presenter.initialize(supportView);
        presenter.helpClicked();

        verify(supportView).goToStream(stream());
    }

    @Test public void shouldNotGoToBlogStreamIfBlogClickedAndBlogHasBeenLoaded() throws Exception {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback callback =
                  (Interactor.ErrorCallback) invocation.getArguments()[1];
                callback.onError(new ShootrException() {
                });
                return null;
            }
        }).when(getBlogInteractor).obtainBlogStream(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));

        presenter.initialize(supportView);
        presenter.blogClicked();

        verify(supportView, never()).goToStream(stream());
    }

    @Test public void shouldNotGoToHelpStreamIfHelpClickedAndHelpHasBeenLoaded() throws Exception {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback callback =
                  (Interactor.ErrorCallback) invocation.getArguments()[1];
                callback.onError(new ShootrException() {});
                return null;
            }
        }).when(getHelpInteractor).obtainHelpStream(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));

        presenter.initialize(supportView);
        presenter.helpClicked();

        verify(supportView, never()).goToStream(stream());
    }

    private Stream stream() {
        Stream stream = new Stream();
        stream.setId(ID_STREAM);
        stream.setTitle(TITLE);
        stream.setAuthorId(ID_AUTHOR);
        return stream;
    }
}
