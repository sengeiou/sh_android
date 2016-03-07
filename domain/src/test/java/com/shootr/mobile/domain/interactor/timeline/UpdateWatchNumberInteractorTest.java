package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.WatchUpdateRequest;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class UpdateWatchNumberInteractorTest {

    @Mock BusPublisher busPublisher;
    @Mock Interactor.CompletedCallback callback;

    private UpdateWatchNumberInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InteractorHandler interactorHandler = new TestInteractorHandler();
        PostExecutionThread postExecutionThread = new TestPostExecutionThread();
        interactor = new UpdateWatchNumberInteractor(interactorHandler, postExecutionThread, busPublisher);
    }

    @Test public void shouldPostInBusPublisher() throws Exception {
        interactor.updateWatchNumber(callback);

        verify(busPublisher).post(any(WatchUpdateRequest.class));
    }
}
