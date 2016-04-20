package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ContributorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class ContributorInteractorTest {

    private static final String STREAM_ID = "streamId";
    private static final String USER_ID = "userId";
    private static final boolean IS_ADDING = true;
    private static final boolean IS_NOT_ADDING = false;
    @Mock Interactor.CompletedCallback callback;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock ContributorRepository contributorRepository;

    private ContributorInteractor contributorInteractor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        contributorInteractor = new ContributorInteractor(interactorHandler, contributorRepository, postExecutionThread);
    }

    @Test public void shouldAddContributorWhenIsAdding() throws Exception {
        contributorInteractor.manageContributor(STREAM_ID, USER_ID, IS_ADDING, callback, errorCallback);

        verify(contributorRepository).addContributor(STREAM_ID, USER_ID);
    }

    @Test public void shouldRemoveContributorWhenIsNotAdding() throws Exception {
        contributorInteractor.manageContributor(STREAM_ID, USER_ID, IS_NOT_ADDING, callback, errorCallback);

        verify(contributorRepository).removeContributor(STREAM_ID, USER_ID);
    }
}
