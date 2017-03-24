package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.BaseMessagePollApiEntity;
import com.shootr.mobile.data.api.entity.EmbedUserApiEntity;
import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.api.entity.UrlApiEntity;
import com.shootr.mobile.data.entity.BaseMessagePollEntity;
import com.shootr.mobile.data.entity.EntitiesEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.UrlEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

import static com.shootr.mobile.domain.utils.Preconditions.checkNotNull;

public class ShotApiEntityMapper {

    @Inject public ShotApiEntityMapper() {
    }

    public ShotEntity transform(ShotApiEntity shotApiEntity) {
        if (shotApiEntity == null) {
            return null;
        }
        ShotEntity shotEntity = new ShotEntity();
        shotEntity.setIdShot(shotApiEntity.getIdShot());
        shotEntity.setComment(shotApiEntity.getComment());
        shotEntity.setImage(shotApiEntity.getImage());
        shotEntity.setImageHeight(shotApiEntity.getImageHeight());
        shotEntity.setImageWidth(shotApiEntity.getImageWidth());
        shotEntity.setType(shotApiEntity.getType());

        EmbedUserApiEntity userApiEntity = shotApiEntity.getUser();
        checkNotNull(userApiEntity,
          "Oh no! Shot from Api didn't have the User embeded! We can't do a proper mapping. idShot=%s",
          shotApiEntity.getIdShot());
        shotEntity.setIdUser(userApiEntity.getIdUser());
        shotEntity.setUsername(userApiEntity.getUserName());
        shotEntity.setUserPhoto(userApiEntity.getPhoto());

        shotEntity.setIdShotParent(shotApiEntity.getIdShotParent());
        shotEntity.setIdUserParent(shotApiEntity.getIdUserParent());
        shotEntity.setUserNameParent(shotApiEntity.getUserNameParent());

        shotEntity.setIdStream(shotApiEntity.getIdStream());
        shotEntity.setStreamTitle(shotApiEntity.getStreamTitle());

        shotEntity.setVideoUrl(shotApiEntity.getVideoUrl());
        shotEntity.setVideoTitle(shotApiEntity.getVideoTitle());
        shotEntity.setVideoDuration(shotApiEntity.getVideoDuration());

        Integer niceCount = shotApiEntity.getNiceCount();
        shotEntity.setNiceCount(niceCount != null ? niceCount : 0);

        shotEntity.setBirth(new Date(shotApiEntity.getBirth()));
        shotEntity.setModified(new Date(shotApiEntity.getModified()));
        shotEntity.setRevision(shotApiEntity.getRevision());

        shotEntity.setProfileHidden(shotApiEntity.getProfileHidden());
        shotEntity.setReplyCount(shotApiEntity.getReplyCount());

        shotEntity.setViews(shotApiEntity.getViews() != null ? shotApiEntity.getViews() : 0);
        shotEntity.setLinkClicks(
            shotApiEntity.getLinkClicks() != null ? shotApiEntity.getLinkClicks() : 0);
        shotEntity.setReshootCounter(
            shotApiEntity.getReshootCount() != null ? shotApiEntity.getReshootCount() : 0);
        shotEntity.setPromoted(
            shotApiEntity.getPromoted() != null ? shotApiEntity.getPromoted() : 0);
        shotEntity.setCtaCaption(shotApiEntity.getCtaCaption());
        shotEntity.setCtaButtonText(shotApiEntity.getCtaButtonText());
        shotEntity.setCtaButtonLink(shotApiEntity.getCtaButtonLink());
        shotEntity.setVerifiedUser(shotApiEntity.getVerifiedUser());
        shotEntity.setPadding((shotApiEntity.getIsPadding()));
        shotEntity.setFromHolder((shotApiEntity.getFromHolder()));
        shotEntity.setFromContributor((shotApiEntity.getFromContributor()));
        setupEntities(shotApiEntity, shotEntity);

        return shotEntity;
    }

    private void setupEntities(ShotApiEntity shotApiEntity, ShotEntity shotEntity) {
        if (shotApiEntity.getEntities() != null) {
            EntitiesEntity entitiesEntity = new EntitiesEntity();
            setupUrls(shotApiEntity, entitiesEntity);
            setupPollsEntities(shotApiEntity, entitiesEntity);
            shotEntity.setEntities(entitiesEntity);
        }
    }

    private void setupPollsEntities(ShotApiEntity shotApiEntity, EntitiesEntity entitiesEntity) {
        ArrayList<BaseMessagePollEntity> pollEntities = new ArrayList<>();

        for (BaseMessagePollApiEntity baseMessagePollApiEntity : shotApiEntity.getEntities()
            .getPolls()) {
            BaseMessagePollEntity baseMessagePollEntity = new BaseMessagePollEntity();
            baseMessagePollEntity.setIdPoll(baseMessagePollApiEntity.getIdPoll());
            baseMessagePollEntity.setIndices(baseMessagePollApiEntity.getIndices());
            baseMessagePollEntity.setPollQuestion(baseMessagePollApiEntity.getPollQuestion());
            pollEntities.add(baseMessagePollEntity);
        }
        entitiesEntity.setPolls(pollEntities);
    }

    private void setupUrls(ShotApiEntity shotApiEntity, EntitiesEntity entitiesEntity) {
        ArrayList<UrlEntity> urlEntities = new ArrayList<>();
        for (UrlApiEntity urlApiEntity : shotApiEntity.getEntities().getUrls()) {
            UrlEntity urlEntity = new UrlEntity();
            urlEntity.setDisplayUrl(urlApiEntity.getDisplayUrl());
            urlEntity.setUrl(urlApiEntity.getUrl());
            urlEntity.setIndices(urlApiEntity.getIndices());
            urlEntities.add(urlEntity);
        }

        entitiesEntity.setUrls(urlEntities);
    }

    public List<ShotEntity> transform(List<ShotApiEntity> shots) {
        List<ShotEntity> entities = new ArrayList<>(shots.size());
        for (ShotApiEntity apiShot : shots) {
            entities.add(transform(apiShot));
        }
        return entities;
    }
}
