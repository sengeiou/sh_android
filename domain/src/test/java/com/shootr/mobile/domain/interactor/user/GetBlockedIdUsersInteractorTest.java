package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.FollowRepository;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetBlockedIdUsersInteractorTest {

    public static final String LIST = "list";
    @Mock FollowRepository remoteFollowRepository;
    @Mock FollowRepository localFollowRepository;
    @Mock Interactor.Callback<List<String>> callback;
    @Mock Interactor.ErrorCallback errorCallback;
    private GetBlockedIdUsersInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetBlockedIdUsersInteractor(interactorHandler, postExecutionThread, localFollowRepository, remoteFollowRepository);
    }

    @Test public void shouldNotLoadRemoteBlockedIdUsersIfThereAreLocalBlockedIdUsers() throws Exception {
        when(localFollowRepository.getBlockedIdUsers()).thenReturn(Collections.singletonList(LIST));

        interactor.loadBlockedIdUsers(callback, errorCallback);

        verify(remoteFollowRepository,never()).getBlockedIdUsers();
    }

    @Test public void shouldLoadRemoteBlockedIdUsersIfThereAreNotLocalBlockedIdUsers() throws Exception {
        when(localFollowRepository.getBlockedIdUsers()).thenReturn(Collections.EMPTY_LIST);

        interactor.loadBlockedIdUsers(callback, errorCallback);

        verify(remoteFollowRepository).getBlockedIdUsers();
    }

}
