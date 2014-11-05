package com.shootr.android.ui.model.mappers;

import android.content.Context;
import com.shootr.android.R;
import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.ui.model.WatchingRequestModel;
import java.util.ArrayList;
import java.util.List;

public class WatchingRequestModelMapper {

    private final String subtitleNoneWatching;
    private final String subtitleOneWatching;
    private final String subtitleTwoWatching;
    private final String subtitleMoreWatching;

    public WatchingRequestModelMapper(Context context) {
        subtitleNoneWatching = context.getString(R.string.watching_request_subtitle_none);
        subtitleOneWatching = context.getString(R.string.watching_request_subtitle_one);
        subtitleTwoWatching = context.getString(R.string.watching_request_subtitle_two);
        subtitleMoreWatching = context.getString(R.string.watching_request_subtitle_more);
    }

    public WatchingRequestModel toWatchingRequestModel(MatchEntity matchEntity, List<UserEntity> userEntities) {
        WatchingRequestModel watchingRequestModel = new WatchingRequestModel();
        watchingRequestModel.setMatchId(matchEntity.getIdMatch());
        watchingRequestModel.setTitle(getTitle(matchEntity));
        watchingRequestModel.setSubtitle(getSubtitle(userEntities));
        watchingRequestModel.setMatchDate(matchEntity.getMatchDate().getTime());
        return watchingRequestModel;
    }

    private String getSubtitle(List<UserEntity> userEntities) {
        List<String> namesFromUserEntities = getNamesFromUserEntities(userEntities);
        int usersWatchingNumber = namesFromUserEntities.size();
        if (usersWatchingNumber == 0) {
            return subtitleNoneWatching;
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
        return String.format("Watching %s", matchTitle);
    }

    private List<String> getNamesFromUserEntities(List<UserEntity> userEntities) {
        List<String> userNames = new ArrayList<>(userEntities.size());
        for (UserEntity userEntity : userEntities) {
            userNames.add(userEntity.getUserName());
        }
        return userNames;
    }
}
