package com.shootr.android.task.jobs.info;

import android.app.Application;

import com.shootr.android.RobolectricGradleTestRunner;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.db.objects.WatchEntity;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.model.mappers.MatchModelMapper;
import com.shootr.android.ui.model.mappers.UserWatchingModelMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
public class InfoListBuilderTest {

    private static final Long CURRENT_USER_ID = 1L;
    private static final Long IRRELEVANT_USER_ID = 2L;

    private static final String USERNAME = "username";
    private static final Long IRRELEVANT_MATCH_ID = 1L;

    private MatchModelMapper matchModelMapper;
    private UserWatchingModelMapper userWatchingModelMapper;
    private Application application;

    @Before
    public void setUp() {
        application = Robolectric.application;
        matchModelMapper = new MatchModelMapper();
        userWatchingModelMapper = new UserWatchingModelMapper(application);
    }

    @Test
    public void thereIsAnEntryForCurrentUserIfHasNoWatchInHisMatch() {
        UserEntity currentUser = getCurrentUser();

        InfoListBuilder infoListBuilder = new InfoListBuilder(currentUser, matchModelMapper, userWatchingModelMapper);

        MatchEntity irrelevantMatch = new MatchEntity();
        irrelevantMatch.setIdMatch(IRRELEVANT_MATCH_ID);
        irrelevantMatch.setMatchDate(new Date());
        irrelevantMatch.setStatus(0L);

        ArrayList<MatchEntity> matches = new ArrayList<>();
        matches.add(irrelevantMatch);

        ArrayList<WatchEntity> watches = new ArrayList<>();
        watches.add(getIrrelevantWatch());

        ArrayList<UserEntity> users = new ArrayList<>();
        users.add(getIrrelevantUser());

        infoListBuilder.setWatches(watches);
        infoListBuilder.provideMatches(matches);
        infoListBuilder.provideUsers(users);

        Map<MatchModel, Collection<UserWatchingModel>> result = infoListBuilder.build();

        Set<MatchModel> matchesInResult = result.keySet();
        assertThat(matchesInResult).hasSize(1);

        MatchModel resultMatch = matchesInResult.iterator().next();
        Collection<UserWatchingModel> usersWatchingResultMatch = result.get(resultMatch);

        assertThat(usersWatchingResultMatch).hasSize(2);

        Iterator<UserWatchingModel> usersIterator = usersWatchingResultMatch.iterator();
        UserWatchingModel user1 = usersIterator.next();
        UserWatchingModel user2 = usersIterator.next();

        assertThat(user1.getIdUser().equals(CURRENT_USER_ID) || user2.getIdUser().equals(CURRENT_USER_ID)).isTrue();
        assertThat(user1.getIdUser()).isNotEqualTo(user2.getIdUser());

        assertThat(user1.getIdUser().equals(CURRENT_USER_ID) && !user1.isWatching() || user2.getIdUser().equals(CURRENT_USER_ID) && !user2.isWatching()).isTrue();
    }

    private UserEntity getCurrentUser() {
        UserEntity currentUser = new UserEntity();
        currentUser.setIdUser(CURRENT_USER_ID);
        currentUser.setUserName(USERNAME);
        return currentUser;
    }

    private UserEntity getIrrelevantUser() {
        UserEntity currentUser = new UserEntity();
        currentUser.setIdUser(IRRELEVANT_USER_ID);
        currentUser.setUserName(USERNAME);
        return currentUser;
    }

    private WatchEntity getIrrelevantWatch() {
        WatchEntity watchEntity = new WatchEntity();
        watchEntity.setIdMatch(IRRELEVANT_MATCH_ID);
        watchEntity.setIdUser(IRRELEVANT_USER_ID);
        watchEntity.setStatus(1L);
        return watchEntity;
    }
}
