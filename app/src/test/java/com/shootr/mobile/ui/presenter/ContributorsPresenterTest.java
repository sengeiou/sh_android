package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.interactor.user.contributor.GetContributorsInteractor;
import com.shootr.mobile.domain.interactor.user.contributor.ManageContributorsInteractor;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.ContributorsView;
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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ContributorsPresenterTest {

    public static final String ID_STREAM = "idStream";
    private static final String ID_USER = "idUser";
    public static final boolean IS_HOLDER = true;
    public static final boolean IS_NOT_HOLDER = false;
    @Mock GetContributorsInteractor getContributorsInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock ContributorsView contributorsView;
    @Mock StreamJoinDateFormatter streamJoinDateFormatter;
    @Mock ManageContributorsInteractor manageContributorsInteractor;
    @Mock FollowInteractor followInteractor;
    @Mock UnfollowInteractor unfollowInteractor;

    private ContributorsPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper = new UserModelMapper(streamJoinDateFormatter);
        presenter = new ContributorsPresenter(getContributorsInteractor,
            manageContributorsInteractor,
          errorMessageFactory,
          userModelMapper, followInteractor, unfollowInteractor);
    }

    @Test public void shouldHideAddContributorButtonWhenIsNotHolder() throws Exception {
        setupGetContributorsInteractor();

        presenter.initialize(contributorsView, ID_STREAM, IS_NOT_HOLDER);

        verify(contributorsView).hideAddContributorsButton();
    }

    @Test public void shouldNotHideAddContributorButtonWhenIsHolder() throws Exception {
        setupGetContributorsInteractor();

        presenter.initialize(contributorsView, ID_STREAM, IS_HOLDER);

        verify(contributorsView, never()).hideAddContributorsButton();
    }

    @Test public void shouldHideAddContributorTextWhenIsNotHolder() throws Exception {
        setupGetContributorsInteractor();

        presenter.initialize(contributorsView, ID_STREAM, IS_NOT_HOLDER);

        verify(contributorsView).hideAddContributorsText();
    }

    @Test public void shouldNotHideAddContributorTextWhenIsHolder() throws Exception {
        setupGetContributorsInteractor();

        presenter.initialize(contributorsView, ID_STREAM, IS_HOLDER);

        verify(contributorsView, never()).hideAddContributorsText();
    }

    @Test public void shouldRenderAllContributorsWhenContributorsModelsIsNotEmpty() throws Exception {
        setupGetContributorsInteractor();

        presenter.initialize(contributorsView, ID_STREAM, IS_HOLDER);

        verify(contributorsView).renderAllContributors(anyList());
    }

    @Test public void shouldNotRenderAllContributorsWhenContributorsModelsIsEmpty() throws Exception {
        setupGetContributorsInteractorWithEmptyList();

        presenter.initialize(contributorsView, ID_STREAM, IS_HOLDER);

        verify(contributorsView, never()).renderAllContributors(anyList());
    }

    @Test public void shouldShowLimitContributorSnackbarWhenAddContributorsAndCountIsGreaterThanOneHundred()
      throws Exception {
        presenter.initialize(contributorsView, ID_STREAM, IS_HOLDER);

        presenter.onAddContributorClick(101);

        verify(contributorsView).showContributorsLimitSnackbar();
    }

    @Test public void shouldNotShowLimitContributorSnackbarWhenAddContributorsAndCountIsNotGreaterThanOneHundred()
      throws Exception {
        presenter.initialize(contributorsView, ID_STREAM, IS_HOLDER);

        presenter.onAddContributorClick(0);

        verify(contributorsView, never()).showContributorsLimitSnackbar();
    }

    @Test public void shouldShowContexMenuInOnLongClickAndIsHolder() throws Exception {
        presenter.initialize(contributorsView, ID_STREAM, IS_HOLDER);

        presenter.onLongContributorClick(userModel());

        verify(contributorsView).showContextMenu(any(UserModel.class));
    }

    @Test public void shouldNotShowContexMenuInOnLongClickAndIsNotHolder() throws Exception {
        presenter.initialize(contributorsView, ID_STREAM, IS_NOT_HOLDER);

        presenter.onLongContributorClick(userModel());

        verify(contributorsView, never()).showContextMenu(any(UserModel.class));
    }

    private void setupGetContributorsInteractor() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<Contributor>> callback =
                  (Interactor.Callback<List<Contributor>>) invocation.getArguments()[2];
                callback.onLoaded(contributors());
                return null;
            }
        }).when(getContributorsInteractor)
          .obtainContributors(anyString(),
            anyBoolean(),
            any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private void setupGetContributorsInteractorWithEmptyList() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<Contributor>> callback =
                  (Interactor.Callback<List<Contributor>>) invocation.getArguments()[2];
                callback.onLoaded(Collections.<Contributor>emptyList());
                return null;
            }
        }).when(getContributorsInteractor)
          .obtainContributors(anyString(),
            anyBoolean(),
            any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private List<Contributor> contributors() {
        List<Contributor> contributors = new ArrayList<>();
        contributors.add(contributor());
        return contributors;
    }

    private Contributor contributor() {
        Contributor contributor = new Contributor();
        contributor.setUser(user());
        contributor.setIdUser(ID_USER);
        return contributor;
    }

    private User user() {
        User user = new User();
        user.setIdUser(ID_USER);
        return user;
    }

    private UserModel userModel() {
        UserModel userModel = new UserModel();
        userModel.setIdUser(ID_USER);
        return userModel;
    }
}
