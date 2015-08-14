package com.shootr.android.ui.presenter;

import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.GetAllShotsByUserInteractor;
import com.shootr.android.domain.interactor.shot.GetOlderAllShotsByUserInteractor;
import com.shootr.android.domain.interactor.shot.MarkNiceShotInteractor;
import com.shootr.android.domain.interactor.shot.UnmarkNiceShotInteractor;
import com.shootr.android.ui.model.mappers.ShotModelMapper;
import com.shootr.android.ui.views.AllShotsView;
import com.shootr.android.util.ErrorMessageFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class AllShotsPresenterTest {

    public static final String USER_ID = "user_id";
    public static final String AVATAR = "avatar";
    public static final String USERNAME = "username";
    public static final long ANY_TIMESTAMP = 0L;

    @Mock GetAllShotsByUserInteractor getAllShotsByUserInteractor;
    @Mock GetOlderAllShotsByUserInteractor getOlderAllShotsByUserInteractor;
    @Mock MarkNiceShotInteractor markNiceShotInteractor;
    @Mock UnmarkNiceShotInteractor unmarkNiceShotInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock AllShotsView allShotsView;

    private ShotModelMapper shotModelMapper;
    private AllShotsPresenter allShotsPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        shotModelMapper = new ShotModelMapper();
        allShotsPresenter = new AllShotsPresenter(getAllShotsByUserInteractor, getOlderAllShotsByUserInteractor,
          markNiceShotInteractor,
          unmarkNiceShotInteractor,
          errorMessageFactory, shotModelMapper);
        allShotsPresenter.setView(allShotsView);
        allShotsPresenter.setUserId(USER_ID);
    }

    @Test
    public void shouldShowAllShotsWhenLoadAllShots() {
        setupAllShotsInteractorCallback(shotList());

        allShotsPresenter.initialize(allShotsView, USER_ID);

        verify(allShotsView).showShots();
    }

    @Test
    public void shouldHideLoadingWhenLoadAllShots() {
        setupAllShotsInteractorCallback(shotList());

        allShotsPresenter.initialize(allShotsView, USER_ID);

        verify(allShotsView).hideLoading();
    }

    @Test
    public void shouldShowEmptyWhenLoadAllShotsAndNothingFound() {
        setupAllShotsInteractorCallback(emptyShotList());

        allShotsPresenter.initialize(allShotsView, USER_ID);

        verify(allShotsView).showEmpty();
    }

    @Test
    public void shouldHideShotsListWhenLoadAllShotsAndNothingFound() {
        setupAllShotsInteractorCallback(emptyShotList());

        allShotsPresenter.initialize(allShotsView, USER_ID);

        verify(allShotsView).hideShots();
    }

    @Test
    public void shouldShowOlderAllShotsWhenLoadOlderAllShots() {
        setupOlderAllShotsInteractorCallback(shotList());

        allShotsPresenter.loadOlderShots(ANY_TIMESTAMP);

        verify(allShotsView).addOldShots(anyList());
    }

    @Test
    public void shouldHideLoadingOldShotsWhenLoadOlderAllShots() {
        setupOlderAllShotsInteractorCallback(shotList());

        allShotsPresenter.loadOlderShots(ANY_TIMESTAMP);

        verify(allShotsView).hideLoadingOldShots();
    }

    @Test
    public void shouldHideLoadingOldShotsWhenLoadOlderAllShotsAndNothingFound() {
        setupOlderAllShotsInteractorEmptyCallback();

        allShotsPresenter.loadOlderShots(ANY_TIMESTAMP);

        verify(allShotsView).hideLoadingOldShots();
    }

    @Test
    public void shouldHideLoadingOldShotsWhenCommunicationErrorLoadingOlderAllShots() {
        setupOlderAllShotsInteractorErrorCallback();

        allShotsPresenter.loadOlderShots(ANY_TIMESTAMP);

        verify(allShotsView).hideLoadingOldShots();
    }

    @Test
    public void shouldShowErrorWhenCommunicationErrorLoadingOlderAllShots() {
        setupOlderAllShotsInteractorErrorCallback();

        allShotsPresenter.loadOlderShots(ANY_TIMESTAMP);

        verify(allShotsView).showError(anyString());
    }

    @Test
    public void shouldHideLoadingWhenCommunicationErrorLoadingAllShots() {
        setupAllShotsInteractorErrorCallback();

        allShotsPresenter.initialize(allShotsView, USER_ID);

        verify(allShotsView).hideLoading();
    }

    @Test
    public void shouldShowErrorWhenCommunicationErrorLoadingAllShots() {
        setupAllShotsInteractorErrorCallback();

        allShotsPresenter.initialize(allShotsView, USER_ID);

        verify(allShotsView).showError(anyString());
    }

    private List<Shot> emptyShotList() {
        return Collections.emptyList();
    }

    private List<Shot> shotList() {
        Shot shot = new Shot();
        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setAvatar(AVATAR);
        userInfo.setIdUser(USER_ID);
        userInfo.setUsername(USERNAME);
        shot.setUserInfo(userInfo);
        return Arrays.asList(shot);
    }

    private void setupAllShotsInteractorCallback(final List<Shot> shotList) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<Shot>> callback = (Interactor.Callback<List<Shot>>) invocation.getArguments()[1];
                callback.onLoaded(shotList);
                return null;
            }
        }).when(getAllShotsByUserInteractor)
          .loadAllShots(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupOlderAllShotsInteractorCallback(final List<Shot> shotList) {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<Shot>> callback = (Interactor.Callback<List<Shot>>) invocation.getArguments()[2];
                callback.onLoaded(shotList);
                return null;
            }
        }).when(getOlderAllShotsByUserInteractor)
          .loadAllShots(anyString(), anyLong(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupOlderAllShotsInteractorEmptyCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<Shot>> callback = (Interactor.Callback<List<Shot>>) invocation.getArguments()[2];
                callback.onLoaded(emptyShotList());
                return null;
            }
        }).when(getOlderAllShotsByUserInteractor)
          .loadAllShots(anyString(), anyLong(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupOlderAllShotsInteractorErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback callback = (Interactor.ErrorCallback) invocation.getArguments()[3];
                callback.onError(new ServerCommunicationException(new Throwable()));
                return null;
            }
        }).when(getOlderAllShotsByUserInteractor)
          .loadAllShots(anyString(), anyLong(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupAllShotsInteractorErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback callback = (Interactor.ErrorCallback) invocation.getArguments()[2];
                callback.onError(new ServerCommunicationException(new Throwable()));
                return null;
            }
        }).when(getAllShotsByUserInteractor)
          .loadAllShots(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }
}