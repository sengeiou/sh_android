package com.shootr.android.ui.model.mappers;

import android.content.res.Resources;
import com.shootr.android.R;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.ui.model.WatchingRequestModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WatchingRequestModelMapper {

    private final String subtitleNoneWatching;
    private final String subtitleOneWatching;
    private final String subtitleTwoWatching;
    private final String subtitleMoreWatching;

    private final String title;

    @Inject public WatchingRequestModelMapper(Resources resources) {
        subtitleNoneWatching = resources.getString(R.string.watching_request_subtitle_none);
        subtitleOneWatching = resources.getString(R.string.watching_request_subtitle_one);
        subtitleTwoWatching = resources.getString(R.string.watching_request_subtitle_two);
        subtitleMoreWatching = resources.getString(R.string.watching_request_subtitle_more);
        title = resources.getString(R.string.watching_event_question);
    }

    public WatchingRequestModel toWatchingRequestModel(EventEntity eventEntity, List<UserEntity> userEntities) {
        WatchingRequestModel watchingRequestModel = new WatchingRequestModel();
        watchingRequestModel.setEventId(eventEntity.getIdEvent().toString());
        watchingRequestModel.setTitle(getTitle(eventEntity));
        watchingRequestModel.setSubtitle(getSubtitle(userEntities, eventEntity));
        return watchingRequestModel;
    }

    private String getSubtitle(List<UserEntity> userEntities, EventEntity event) {
        List<String> namesFromUserEntities = getNamesFromUserEntities(userEntities);
        int usersWatchingNumber = namesFromUserEntities.size();
        if (usersWatchingNumber == 0) {
            return subtitleNoneWatching;
        } else if (usersWatchingNumber == 1) {
            return String.format(subtitleOneWatching, namesFromUserEntities.get(0));
        } else if (usersWatchingNumber == 2) {
            return String.format(subtitleTwoWatching, namesFromUserEntities.get(0), namesFromUserEntities.get(1));
        } else {
            return String.format(subtitleMoreWatching, usersWatchingNumber);
        }
    }

    private String getTitle(EventEntity eventEntity) {
        String eventTitle = eventEntity.getTitle();
        return String.format(title, eventTitle);
    }

    private List<String> getNamesFromUserEntities(List<UserEntity> userEntities) {
        List<String> userNames = new ArrayList<>(userEntities.size());
        for (UserEntity userEntity : userEntities) {
            userNames.add(userEntity.getUserName());
        }
        return userNames;
    }
}
