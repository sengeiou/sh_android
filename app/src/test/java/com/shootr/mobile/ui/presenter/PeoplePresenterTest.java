package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.GetMutualsInteractor;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.PeopleView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class PeoplePresenterTest {

    private static final String ID_USER = "idUser";
    @Mock Bus bus;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock StreamJoinDateFormatter streamJoinDateFormatter;
    @Mock GetMutualsInteractor getMutualsInteractor;
    private PeoplePresenter presenter;
    @Mock PeopleView view;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper = new UserModelMapper(streamJoinDateFormatter);

        presenter = new PeoplePresenter(bus, errorMessageFactory, getMutualsInteractor, userModelMapper);
        presenter.setView(view);
    }

    @Test public void shouldShowPeopleListOnPeopleListLoaded() throws Exception {
        setupMutualsInteractor();

        presenter.initialize();

        verify(view).showPeopleList();
    }

    @Test public void shouldShowPeopleListOnResume() throws Exception {
        setupMutualsInteractor();

        presenter.pause();
        presenter.resume();

        verify(view).showPeopleList();
    }

    private void setupMutualsInteractor() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[0];
                callback.onLoaded(mutuals());
                return null;
            }
        }).when(getMutualsInteractor)
          .obtainMutuals(any(Interactor.Callback.class));
    }

    private List<User> mutuals() {
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
}
