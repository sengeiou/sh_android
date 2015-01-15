package com.shootr.android.ui.model.mappers;

import android.content.res.Resources;
import com.shootr.android.R;
import com.shootr.android.data.entity.MatchEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.ui.model.WatchingRequestModel;
import com.shootr.android.util.TimeUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WatchingRequestModelMapper {

    private final String subtitleNoneWatching;
    private final String subtitleNoneWatchingAndNotStarted;
    private final String subtitleOneWatching;
    private final String subtitleTwoWatching;
    private final String subtitleMoreWatching;

    private final TimeUtils timeUtils;

    private final String title;

    @Inject public WatchingRequestModelMapper(Resources resources, TimeUtils timeUtils) {
        this.timeUtils = timeUtils;
        subtitleNoneWatching = resources.getString(R.string.watching_request_subtitle_none);
        subtitleOneWatching = resources.getString(R.string.watching_request_subtitle_one);
        subtitleTwoWatching = resources.getString(R.string.watching_request_subtitle_two);
        subtitleMoreWatching = resources.getString(R.string.watching_request_subtitle_more);
        subtitleNoneWatchingAndNotStarted = resources.getString(R.string.watching_request_subtitle_none_and_not_started);
        title = resources.getString(R.string.watching_match_question);
    }

    public WatchingRequestModel toWatchingRequestModel(MatchEntity matchEntity, List<UserEntity> userEntities) {
        WatchingRequestModel watchingRequestModel = new WatchingRequestModel();
        watchingRequestModel.setMatchId(matchEntity.getIdMatch());
        watchingRequestModel.setTitle(getTitle(matchEntity));
        watchingRequestModel.setSubtitle(getSubtitle(userEntities, matchEntity));
        watchingRequestModel.setMatchDate(matchEntity.getMatchDate().getTime());
        return watchingRequestModel;
    }

    private String getSubtitle(List<UserEntity> userEntities, MatchEntity match) {
        List<String> namesFromUserEntities = getNamesFromUserEntities(userEntities);
        int usersWatchingNumber = namesFromUserEntities.size();
        boolean hasStarted = match.getMatchDate().before(timeUtils.getCurrentDate());
        if (usersWatchingNumber == 0 && hasStarted) {
            return subtitleNoneWatching;
        }else if(usersWatchingNumber == 0 && !hasStarted){
            return subtitleNoneWatchingAndNotStarted;
        }else if (usersWatchingNumber == 1) {
            return String.format(subtitleOneWatching, namesFromUserEntities.get(0));
        } else if (usersWatchingNumber == 2) {
            return String.format(subtitleTwoWatching, namesFromUserEntities.get(0), namesFromUserEntities.get(1));
        } else {
            return String.format(subtitleMoreWatching, usersWatchingNumber);
        }
    }

    private String getTitle(MatchEntity matchEntity) {
        String matchTitle =  matchEntity.getLocalTeamName() + "-" + matchEntity.getVisitorTeamName();
        return String.format(title, matchTitle);
    }

    private List<String> getNamesFromUserEntities(List<UserEntity> userEntities) {
        List<String> userNames = new ArrayList<>(userEntities.size());
        for (UserEntity userEntity : userEntities) {
            userNames.add(userEntity.getUserName());
        }
        return userNames;
    }
}
