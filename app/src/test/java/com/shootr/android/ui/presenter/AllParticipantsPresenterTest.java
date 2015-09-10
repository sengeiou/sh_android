package com.shootr.android.ui.presenter;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.android.domain.interactor.user.GetAllParticipantsInteractor;
import com.shootr.android.domain.utils.DateRangeTextProvider;
import com.shootr.android.domain.utils.StreamJoinDateFormatter;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.AllParticipantsView;
import com.shootr.android.util.ErrorMessageFactory;
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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class AllParticipantsPresenterTest {

    public static final String STRING_ID = "stringId";
    public static final String ID_USER = "idUser";
    @Mock GetAllParticipantsInteractor getAllParticipantsInteractor;
    @Mock SelectStreamInteractor selectStreamInteractor;
    @Mock FollowFaketeractor followFaketeractor;
    @Mock UnfollowFaketeractor unfollowFaketeractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock AllParticipantsView allParticipantsView;
    @Mock DateRangeTextProvider dateRangeTextProvider;
    @Mock TimeUtils timeUtils;

    private AllParticipantsPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper = new UserModelMapper(new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils));
        presenter = new AllParticipantsPresenter(getAllParticipantsInteractor, selectStreamInteractor, followFaketeractor, unfollowFaketeractor, errorMessageFactory, userModelMapper);
        presenter.setView(allParticipantsView);
    }

    @Test
    public void shouldHideEmptyWhenLoadAllParticipants() throws Exception {
        presenter.initialize(allParticipantsView, STRING_ID);

        verify(allParticipantsView).hideEmpty();
    }

    @Test
    public void shouldShowLoadingWhenLoadAllParticipants() throws Exception {
        presenter.initialize(allParticipantsView, STRING_ID);

        verify(allParticipantsView).showLoading();
    }

    @Test
    public void shouldHideLoadingWhenAllParticipantsLoaded() throws Exception {
        setupAllParticipantsEmptyCallback();

        presenter.initialize(allParticipantsView, STRING_ID);

        verify(allParticipantsView).hideLoading();
    }

    @Test
    public void shouldShowAllParticipantsListWhenAllParticipantsLoaded() throws Exception {
        setupAllParticipantsEmptyCallback();

        presenter.initialize(allParticipantsView, STRING_ID);

        verify(allParticipantsView).showAllParticipantsList();
    }

    @Test
    public void shouldShowEmptyWhenAllParticipantsReceivesEmptyList() throws Exception {
        setupAllParticipantsEmptyCallback();

        presenter.initialize(allParticipantsView, STRING_ID);

        verify(allParticipantsView).showEmpty();
    }

    @Test
    public void shouldRenderParticipantsWhenAllParticipantsLoaded() throws Exception {
        setupAllParticipantsCallback();

        presenter.initialize(allParticipantsView, STRING_ID);

        verify(allParticipantsView).renderAllParticipants(anyList());
    }

    @Test
    public void shouldShowErrorWhenAllParticipantsLoadedAndServerCommunicationException() throws Exception {
        setupAllParticipantsErrorCallback();

        presenter.initialize(allParticipantsView, STRING_ID);

        verify(allParticipantsView).showError(anyString());
    }

    @Test
    public void shouldHideEmptyWhenRefreshAllParticipants() throws Exception {
        setupAllParticipantsCallback();

        presenter.refreshAllParticipants();

        verify(allParticipantsView).hideEmpty();
    }

    @Test
    public void shouldRenderParticipantsWhenRefreshAllParticipants() throws Exception {
        setupAllParticipantsCallback();

        presenter.refreshAllParticipants();

        verify(allParticipantsView).renderAllParticipants(anyList());
    }

    @Test
    public void shouldSelectStreamWhenResume() throws Exception {
        setupAllParticipantsCallback();

        presenter.pause();
        presenter.resume();

        verify(selectStreamInteractor).selectStream(anyString(), any(Interactor.Callback.class));
    }

    @Test
    public void shouldRenderParticipantsWhenFollowAParticipant() throws Exception {
        setupAllParticipantsCallback();

        presenter.initialize(allParticipantsView, STRING_ID);
        presenter.followUser(userModel());

        verify(allParticipantsView).renderAllParticipants(anyList());
    }

    @Test
    public void shouldRenderParticipantsWhenUnfollowAParticipant() throws Exception {
        setupAllParticipantsCallback();

        presenter.initialize(allParticipantsView, STRING_ID);
        presenter.unfollowUser(userModel());

        verify(allParticipantsView).renderAllParticipants(anyList());
    }

    private void setupAllParticipantsEmptyCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[3];
                callback.onLoaded(noParticipants());
                return null;
            }
        }).when(getAllParticipantsInteractor).obtainAllParticipants(anyString(),
          anyLong(),
          anyBoolean(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupAllParticipantsCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[3];
                callback.onLoaded(participants());
                return null;
            }
        }).when(getAllParticipantsInteractor).obtainAllParticipants(anyString(),
          anyLong(),
          anyBoolean(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupAllParticipantsErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[4];
                errorCallback.onError(new ShootrException() {
                });
                return null;
            }
        }).when(getAllParticipantsInteractor).obtainAllParticipants(anyString(),
          anyLong(),
          anyBoolean(),
          any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private List<User> participants() {
        List<User> participants = new ArrayList<>();
        participants.add(user());
        participants.add(new User());
        return participants;
    }

    private User user() {
        User user = new User();
        user.setIdUser(ID_USER);
        return user;
    }

    private List<User> noParticipants() {
        return Collections.EMPTY_LIST;
    }

    private UserModel userModel() {
        UserModel userModel = new UserModel();
        userModel.setIdUser(ID_USER);
        return userModel;
    }


}
