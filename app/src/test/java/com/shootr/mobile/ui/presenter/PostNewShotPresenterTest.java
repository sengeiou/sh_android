package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.PostNewShotAsReplyInteractor;
import com.shootr.mobile.domain.interactor.shot.PostNewShotInStreamInteractor;
import com.shootr.mobile.domain.interactor.user.GetMentionedPeopleInteractor;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.PostNewShotView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PostNewShotPresenterTest {

    private static final String ID_USER = "idUser";
    public static final String USERNAME = "username";
    public static final String PART_OF_A_USERNAME = "@use";
    public static final String COMMENT_WITH_PART_OF_USERNAME = "comment @use";
    public static final String COMMENT_WITH_USERNAME = "comment @username ";
    private PostNewShotPresenter presenter;

    @Mock Bus bus;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock GetMentionedPeopleInteractor getMentionedPeopleInteractor;
    @Mock PostNewShotInStreamInteractor postNewShotInStreamInteractor;
    @Mock PostNewShotAsReplyInteractor postNewShotAsReplyInteractor;
    @Mock StreamJoinDateFormatter streamJoinDateFormatter;
    @Mock PostNewShotView postNewShotView;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper = new UserModelMapper(streamJoinDateFormatter);
        presenter = new PostNewShotPresenter(bus, errorMessageFactory, postNewShotInStreamInteractor, postNewShotAsReplyInteractor, getMentionedPeopleInteractor, userModelMapper);
        presenter.setView(postNewShotView);
    }

    @Test public void shouldShowMentionSuggestionsIfPeopleObtainedWhenMentioning() throws Exception {
        setupMentionedPeopleCallback();

        presenter.autocompleteMention(USERNAME);

        verify(postNewShotView).showMentionSuggestions();
    }

    @Test public void shouldHideImageContainerIfPeopleObtainedWhenMentioning() throws Exception {
        setupMentionedPeopleCallback();

        presenter.autocompleteMention(USERNAME);

        verify(postNewShotView).hideImageContainer();

    }

    @Test public void shouldRenderMentionSuggestionsIfPeopleObtainedWhenMentioning() throws Exception {
        setupMentionedPeopleCallback();

        presenter.autocompleteMention(USERNAME);

        verify(postNewShotView).renderMentionSuggestions(anyList());

    }

    @Test public void shouldhideMentionSuggestionsIfNoPeopleObtainedWhenMentioning() throws Exception {
        setupNoMentionedPeopleCallback();

        presenter.autocompleteMention(USERNAME);

        verify(postNewShotView).hideMentionSuggestions();
    }

    @Test public void shouldMentionUserWhenMentionClicked() throws Exception {
        setupMentionedPeopleCallback();

        presenter.autocompleteMention(PART_OF_A_USERNAME);
        presenter.onMentionClicked(userModel(), COMMENT_WITH_PART_OF_USERNAME);

        verify(postNewShotView).mentionUser(COMMENT_WITH_USERNAME);
    }

    @Test public void shouldHideMentionSuggestionsWhenMentionClicked() throws Exception {
        setupMentionedPeopleCallback();

        presenter.autocompleteMention(PART_OF_A_USERNAME);
        presenter.onMentionClicked(userModel(), COMMENT_WITH_PART_OF_USERNAME);

        verify(postNewShotView).hideMentionSuggestions();
    }

    @Test public void shouldShowImageContainerWhenMentionClickedIfThereWasNoPictureSelected() throws Exception {
        setupMentionedPeopleCallback();

        presenter.autocompleteMention(PART_OF_A_USERNAME);
        presenter.onMentionClicked(userModel(), COMMENT_WITH_PART_OF_USERNAME);

        verify(postNewShotView, never()).showImageContainer();
    }

    @Test public void shouldSetCursonrToEndOfTextWhenMentionClicked() throws Exception {
        setupMentionedPeopleCallback();

        presenter.autocompleteMention(PART_OF_A_USERNAME);
        presenter.onMentionClicked(userModel(), COMMENT_WITH_PART_OF_USERNAME);

        verify(postNewShotView).setCursorToEndOfText();
    }

    @Test public void shouldHideMentionSuggestionsWhenStopMentioning() throws Exception {
        presenter.onStopMentioning();

        verify(postNewShotView).hideMentionSuggestions();
    }

    @Test public void shouldNotShowImageContainerWhenStopMentioningIfThereWasNoImageSelected() throws Exception {
        presenter.onStopMentioning();

        verify(postNewShotView, never()).showImageContainer();
    }

    private List<User> mentionSuggestions() {
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

    private UserModel userModel() {
        UserModel userModel = new UserModel();
        userModel.setIdUser(ID_USER);
        userModel.setUsername(USERNAME);
        return userModel;
    }

    public void setupMentionedPeopleCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[1];
                callback.onLoaded(mentionSuggestions());
                return null;
            }
        }).when(getMentionedPeopleInteractor).obtainMentionedPeople(anyString(), any(Interactor.Callback.class));
    }

    public void setupNoMentionedPeopleCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[1];
                callback.onLoaded(Collections.<User>emptyList());
                return null;
            }
        }).when(getMentionedPeopleInteractor).obtainMentionedPeople(anyString(), any(Interactor.Callback.class));
    }
}
