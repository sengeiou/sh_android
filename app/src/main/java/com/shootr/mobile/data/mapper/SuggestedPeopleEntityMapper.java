package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.SuggestedPeopleEntity;
import com.shootr.mobile.domain.SuggestedPeople;
import com.shootr.mobile.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SuggestedPeopleEntityMapper {

    @Inject public SuggestedPeopleEntityMapper() {
    }

    public SuggestedPeople transform(SuggestedPeopleEntity suggestedPeopleEntity, String currentUserId, boolean isFollower, boolean isFollowing) {
        if (suggestedPeopleEntity == null) {
            return null;
        }
        SuggestedPeople suggestedPeople = new SuggestedPeople();
        User user = new User();

        user.setIdUser(suggestedPeopleEntity.getIdUser());
        user.setUsername(suggestedPeopleEntity.getUserName());
        user.setName(suggestedPeopleEntity.getName());
        user.setPhoto(suggestedPeopleEntity.getPhoto());
        user.setNumFollowings(suggestedPeopleEntity.getNumFollowings());
        user.setNumFollowers(suggestedPeopleEntity.getNumFollowers());
        user.setWebsite(suggestedPeopleEntity.getWebsite());
        user.setBio(suggestedPeopleEntity.getBio());
        user.setPoints(suggestedPeopleEntity.getPoints());
        user.setVerifiedUser(suggestedPeopleEntity.getVerifiedUser() == 1);

        user.setIdWatchingStream(suggestedPeopleEntity.getIdWatchingStream());
        user.setWatchingStreamTitle(suggestedPeopleEntity.getWatchingStreamTitle());

        user.setJoinStreamDate(suggestedPeopleEntity.getJoinStreamDate());
        user.setCreatedStreamsCount(suggestedPeopleEntity.getCreatedStreamsCount());
        user.setFavoritedStreamsCount(suggestedPeopleEntity.getFavoritedStreamsCount());

        suggestedPeople.setRelevance(suggestedPeopleEntity.getRelevance());
        suggestedPeople.setUser(user);

        return suggestedPeople;
    }

    public SuggestedPeopleEntity transform(SuggestedPeople suggestedPeople) {
        if (suggestedPeople == null) {
            return null;
        }
        SuggestedPeopleEntity suggestedPeopleEntity = new SuggestedPeopleEntity();
        suggestedPeopleEntity.setIdUser(suggestedPeople.getUser().getIdUser());
        suggestedPeopleEntity.setUserName(suggestedPeople.getUser().getUsername());
        suggestedPeopleEntity.setName(suggestedPeople.getUser().getName());
        suggestedPeopleEntity.setEmail(suggestedPeople.getUser().getEmail());
        suggestedPeopleEntity.setPhoto(suggestedPeople.getUser().getPhoto());
        suggestedPeopleEntity.setPoints(suggestedPeople.getUser().getPoints());
        suggestedPeopleEntity.setVerifiedUser(suggestedPeople.getUser().isVerifiedUser() ? 1:0);
        suggestedPeopleEntity.setNumFollowings(suggestedPeople.getUser().getNumFollowings());
        suggestedPeopleEntity.setNumFollowers(suggestedPeople.getUser().getNumFollowers());
        suggestedPeopleEntity.setWebsite(suggestedPeople.getUser().getWebsite());
        suggestedPeopleEntity.setBio(suggestedPeople.getUser().getBio());
        suggestedPeopleEntity.setCreatedStreamsCount(suggestedPeople.getUser().getCreatedStreamsCount());
        suggestedPeopleEntity.setFavoritedStreamsCount(suggestedPeople.getUser().getFavoritedStreamsCount());

        suggestedPeopleEntity.setIdWatchingStream(suggestedPeople.getUser().getIdWatchingStream());

        suggestedPeopleEntity.setWatchingStreamTitle(suggestedPeople.getUser().getWatchingStreamTitle());

        suggestedPeopleEntity.setJoinStreamDate(suggestedPeople.getUser().getJoinStreamDate());

        suggestedPeopleEntity.setRelevance(suggestedPeople.getRelevance());

        return suggestedPeopleEntity;
    }

    public List<SuggestedPeopleEntity> transform(List<SuggestedPeople> suggestedPeoples) {
        List<SuggestedPeopleEntity> suggestedPeopleEntities = new ArrayList<>(suggestedPeoples.size());
        SuggestedPeopleEntity suggestedPeopleEntity;
        for (SuggestedPeople suggestedPeople : suggestedPeoples) {
            suggestedPeopleEntity = transform(suggestedPeople);
            if (suggestedPeopleEntity != null) {
                suggestedPeopleEntities.add(suggestedPeopleEntity);
            }
        }
        return suggestedPeopleEntities;
    }

    public SuggestedPeople transform(SuggestedPeopleEntity suggestedPeopleEntity, String idCurrentUser) {
        return transform(suggestedPeopleEntity, idCurrentUser, false, false);
    }

    public SuggestedPeople transform(SuggestedPeopleEntity suggestedPeopleEntity) {
        return transform(suggestedPeopleEntity, "-1L", false, false);
    }
}
