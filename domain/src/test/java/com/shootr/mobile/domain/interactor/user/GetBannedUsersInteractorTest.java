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

public class GetBannedUsersInteractorTest {

    public static final String LIST = "list";
    @Mock FollowRepository remoteFollowRepository;
    @Mock FollowRepository localFollowRepository;
    @Mock Interactor.Callback<List<String>> callback;
    @Mock Interactor.ErrorCallback errorCallback;
    private GetBannedUsersInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new GetBannedUsersInteractor(interactorHandler, postExecutionThread, localFollowRepository, remoteFollowRepository);
    }

    @Test public void shouldNotLoadRemoteBannedIdUsersIfThereAreLocalBlockedIdUsers() throws Exception {
        when(localFollowRepository.getBannedIdUsers()).thenReturn(Collections.singletonList(LIST));

        interactor.loadBannedIdUsers(callback, errorCallback);

        verify(remoteFollowRepository,never()).getBannedIdUsers();
    }

    @Test public void shouldLoadRemoteBannedIdUsersIfThereAreNotLocalBlockedIdUsers() throws Exception {
        when(localFollowRepository.getBannedIdUsers()).thenReturn(Collections.EMPTY_LIST);

        interactor.loadBannedIdUsers(callback, errorCallback);

        verify(remoteFollowRepository).getBannedIdUsers();
    }
}
