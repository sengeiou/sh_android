package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.GetNicersInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.NicersView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class NicersPresenterTest {

    public static final String ID_SHOT = "stringId";
    public static final String ID_USER = "idUser";

    @Mock GetNicersInteractor getNicersInteractor;
    @Mock FollowInteractor followInteractor;
    @Mock UnfollowInteractor unfollowInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock NicersView nicersView;
    @Mock UserModelMapper userModelMapper;
    @Mock DateRangeTextProvider dateRangeTextProvider;
    @Mock TimeUtils timeUtils;

    private NicersPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper =
          new UserModelMapper(new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils));
        presenter = new NicersPresenter(getNicersInteractor,
          followInteractor,
          unfollowInteractor,
          errorMessageFactory,
          userModelMapper);
        presenter.setView(nicersView);
    }

    @Test
    public void shouldHideEmptyWhenLoadNicers() throws Exception {
        presenter.initialize(nicersView, ID_SHOT);

        verify(nicersView).hideEmpty();
    }

    @Test
    public void shouldShowLoadingWhenLoadNicers() throws Exception {
        presenter.initialize(nicersView, ID_SHOT);

        verify(nicersView).showLoading();
    }

    @Test
    public void shouldHideLoadingWhenNicersLoaded() throws Exception {
        setupNicersEmptyCallback();

        presenter.initialize(nicersView, ID_SHOT);

        verify(nicersView).hideLoading();
    }

    @Test
    public void shouldShowEmptyWhenNicersReceivesEmptyList() throws Exception {
        setupNicersEmptyCallback();

        presenter.initialize(nicersView, ID_SHOT);

        verify(nicersView).showEmpty();
    }

    @Test
    public void shouldRenderNicersWhenNicersLoaded() throws Exception {
        setupNicersCallback();

        presenter.initialize(nicersView, ID_SHOT);

        verify(nicersView).renderNicers(anyList());
    }

    @Test
    public void shouldShowErrorWhenNicersLoadedAndServerCommunicationException() throws Exception {
        setupNicersErrorCallback();

        presenter.initialize(nicersView, ID_SHOT);

        verify(nicersView).showError(anyString());
    }

    @Test
    public void shouldRenderNicersWhenResume() throws Exception {
        setupNicersCallback();

        presenter.pause();
        presenter.resume();

        verify(nicersView).renderNicers(anyList());
    }

    @Test
    public void shouldRenderNicersWhenFollowANicer() throws Exception {
        setupNicersCallback();

        presenter.initialize(nicersView, ID_SHOT);
        presenter.followUser(userModel());

        verify(nicersView).renderNicers(anyList());
    }

    @Test
    public void shouldRenderNicersWhenUnfollowANicer() throws Exception {
        setupNicersCallback();

        presenter.initialize(nicersView, ID_SHOT);
        presenter.unfollowUser(userModel());

        verify(nicersView).renderNicers(anyList());
    }

    private List<User> nicers() {
        List<User> nicers = new ArrayList<>();
        nicers.add(user());
        nicers.add(new User());
        return nicers;
    }

    private User user() {
        User user = new User();
        user.setIdUser(ID_USER);
        return user;
    }

    private List<User> noNicers() {
        return Collections.EMPTY_LIST;
    }

    private UserModel userModel() {
        UserModel userModel = new UserModel();
        userModel.setIdUser(ID_USER);
        return userModel;
    }

    private void setupNicersEmptyCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[1];
                callback.onLoaded(noNicers());
                return null;
            }
        }).when(getNicersInteractor).obtainNicersWithUser(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupNicersCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[1];
                callback.onLoaded(nicers());
                return null;
            }
        }).when(getNicersInteractor).obtainNicersWithUser(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupNicersErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[2];
                errorCallback.onError(new ShootrException() {
                });
                return null;
            }
        }).when(getNicersInteractor).obtainNicersWithUser(anyString(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }
}
