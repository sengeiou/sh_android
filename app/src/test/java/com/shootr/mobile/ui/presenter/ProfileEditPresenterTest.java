package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.user.GetUserByIdInteractor;
import com.shootr.mobile.domain.interactor.user.UpdateUserProfileInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.ProfileEditView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import dagger.ObjectGraph;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileEditPresenterTest {

    private static final String ID_USER = "idUser";
    private static final String USERNAME = "userName";
    private static final String BIO = "BIO";
    private static final String LONG_BIO = "BIOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO";
    @Mock SessionRepository sessionRepository;
    @Mock Bus bus;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock InteractorHandler interactorHandler;
    @Mock GetUserByIdInteractor getUserByIdInteractor;
    @Mock UpdateUserProfileInteractor updateUserProfileInteractor;
    @Mock ProfileEditView profileEditView;
    @Mock ObjectGraph objectGraph;
    @Mock StreamJoinDateFormatter streamJoinDateFormatter;

    private ProfileEditPresenter presenter;


    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper = new UserModelMapper(streamJoinDateFormatter);
        presenter = new ProfileEditPresenter(sessionRepository,
          userModelMapper,
          bus,
          errorMessageFactory,
          interactorHandler,
          getUserByIdInteractor,
          updateUserProfileInteractor);
    }

    @Test public void shouldShowUpdatedSuccessfulAlertWhenDoneAndAllIsValidated() throws Exception {
        setupUpdateUserProfileInteractor();
        setUpGetUserByIdInteractor();
        when(sessionRepository.getCurrentUser()).thenReturn(user());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(profileEditView.getBio()).thenReturn(BIO);
        when(profileEditView.getWebsite()).thenReturn("");
        when(profileEditView.getUsername()).thenReturn("USERNAME");
        when(profileEditView.getName()).thenReturn("NAME");

        presenter.initialize(profileEditView, objectGraph);

        presenter.done();

        verify(profileEditView).showUpdatedSuccessfulAlert();
    }

    @Test public void shouldNotShowUpdatedSuccessfulAlertWhenUserNameAndWebIsEmpty() throws Exception {
        setupUpdateUserProfileInteractor();
        setUpGetUserByIdInteractor();
        when(sessionRepository.getCurrentUser()).thenReturn(user());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(profileEditView.getBio()).thenReturn(BIO);
        when(profileEditView.getWebsite()).thenReturn("");
        when(profileEditView.getUsername()).thenReturn("");
        when(profileEditView.getName()).thenReturn("NAME");

        presenter.initialize(profileEditView,objectGraph);
        presenter.done();

        verify(profileEditView, never()).showUpdatedSuccessfulAlert();
    }

    @Test public void shouldNotShowUpdatedSuccessfulAlertWhenWebIsNotValid() throws Exception {
        setupUpdateUserProfileInteractor();
        setUpGetUserByIdInteractor();
        when(sessionRepository.getCurrentUser()).thenReturn(user());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(profileEditView.getBio()).thenReturn(BIO);
        when(profileEditView.getWebsite()).thenReturn("frff");
        when(profileEditView.getUsername()).thenReturn("USERNAME");
        when(profileEditView.getName()).thenReturn("NAME");

        presenter.initialize(profileEditView,objectGraph);
        presenter.done();

        verify(profileEditView, never()).showUpdatedSuccessfulAlert();
    }

    @Test public void shouldShowUpdatedSuccessfulAlertWhenWebIsValid() throws Exception {
        setupUpdateUserProfileInteractor();
        setUpGetUserByIdInteractor();
        when(sessionRepository.getCurrentUser()).thenReturn(user());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(profileEditView.getBio()).thenReturn(BIO);
        when(profileEditView.getWebsite()).thenReturn("www.aaa.com");
        when(profileEditView.getUsername()).thenReturn("USERNAME");
        when(profileEditView.getName()).thenReturn("NAME");

        presenter.initialize(profileEditView,objectGraph);
        presenter.done();

        verify(profileEditView).showUpdatedSuccessfulAlert();
    }

    @Test public void shouldShowUpdatedSuccessfulAlertWhenWebIsValidated() throws Exception {
        setupUpdateUserProfileInteractor();
        setUpGetUserByIdInteractor();
        when(sessionRepository.getCurrentUser()).thenReturn(user());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(profileEditView.getBio()).thenReturn(BIO);
        when(profileEditView.getWebsite()).thenReturn("www.aaa.com");
        when(profileEditView.getUsername()).thenReturn("USERNAME");
        when(profileEditView.getName()).thenReturn("NAME");

        presenter.initialize(profileEditView,objectGraph);
        presenter.done();

        verify(profileEditView).showUpdatedSuccessfulAlert();
    }

    @Test public void shouldShowUpdatedSuccessfulAlertWhenNameIsEmpty() throws Exception {
        setupUpdateUserProfileInteractor();
        setUpGetUserByIdInteractor();
        when(sessionRepository.getCurrentUser()).thenReturn(user());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(profileEditView.getBio()).thenReturn(BIO);
        when(profileEditView.getWebsite()).thenReturn("www.aaa.com");
        when(profileEditView.getUsername()).thenReturn("USERNAME");
        when(profileEditView.getName()).thenReturn("");

        presenter.initialize(profileEditView,objectGraph);
        presenter.done();

        verify(profileEditView).showUpdatedSuccessfulAlert();
    }

    @Test public void shouldNotShowUpdatedSuccesfulAlertWhenBioIsNotValid() throws Exception {
        setupUpdateUserProfileInteractor();
        setUpGetUserByIdInteractor();
        when(sessionRepository.getCurrentUser()).thenReturn(user());
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
        when(profileEditView.getBio()).thenReturn(LONG_BIO);
        when(profileEditView.getWebsite()).thenReturn("www.aaa.com");
        when(profileEditView.getUsername()).thenReturn("USERNAME");
        when(profileEditView.getName()).thenReturn("");

        presenter.initialize(profileEditView,objectGraph);
        presenter.done();

        verify(profileEditView, never()).showUpdatedSuccessfulAlert();
    }

    private void setupUpdateUserProfileInteractor(){
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.CompletedCallback) invocation.getArguments()[1]).onCompleted();
                return null;
            }
        }).when(updateUserProfileInteractor).updateProfile(any(User.class),
          any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setUpGetUserByIdInteractor(){
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                ((GetUserByIdInteractor.Callback) invocation.getArguments()[1]).onLoaded(user());
                return null;
            }
        }).when(getUserByIdInteractor).loadUserById(anyString(), any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
    }

    private User user(){
        User user = new User();
        user.setIdUser(ID_USER);
        user.setUsername(USERNAME);
        user.setEmailConfirmed(false);
        user.setBio(" ");
        return user;
    }
}
