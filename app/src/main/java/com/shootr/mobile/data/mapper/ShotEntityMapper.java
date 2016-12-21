package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.ShotDetailEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotDetail;
import com.shootr.mobile.domain.repository.nice.InternalNiceShotRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class ShotEntityMapper {

    private final InternalNiceShotRepository niceShotRepository;
    private final MetadataMapper metadataMapper;

    @Inject public ShotEntityMapper(InternalNiceShotRepository niceShotRepository, MetadataMapper metadataMapper) {
        this.niceShotRepository = niceShotRepository;
        this.metadataMapper = metadataMapper;
    }

    public Shot transform(ShotEntity shotEntity) {
        if (shotEntity == null) {
            return null;
        }
        Shot shot = new Shot();
        shot.setIdShot(shotEntity.getIdShot());
        shot.setComment(shotEntity.getComment());
        shot.setImage(shotEntity.getImage());
        shot.setImageHeight(shotEntity.getImageHeight());
        shot.setImageWidth(shotEntity.getImageWidth());
        shot.setPublishDate(shotEntity.getBirth());
        if (shotEntity.getIdStream() != null) {
            Shot.ShotStreamInfo eventInfo = new Shot.ShotStreamInfo();
            eventInfo.setIdStream(shotEntity.getIdStream());
            eventInfo.setStreamTitle(shotEntity.getStreamTitle());
            shot.setStreamInfo(eventInfo);
        }
        BaseMessage.BaseMessageUserInfo userInfo = new BaseMessage.BaseMessageUserInfo();
        userInfo.setIdUser(shotEntity.getIdUser());
        userInfo.setUsername(shotEntity.getUsername());
        userInfo.setAvatar(shotEntity.getUserPhoto());
        userInfo.setVerifiedUser(shotEntity.getVerifiedUser());
        shot.setUserInfo(userInfo);
        shot.setParentShotId(shotEntity.getIdShotParent());
        shot.setParentShotUserId(shotEntity.getIdUserParent());
        shot.setParentShotUsername(shotEntity.getUserNameParent());
        shot.setVideoUrl(shotEntity.getVideoUrl());
        shot.setVideoTitle(shotEntity.getVideoTitle());
        shot.setVideoDuration(shotEntity.getVideoDuration());
        shot.setType(shotEntity.getType());
        shot.setNiceCount(shotEntity.getNiceCount());
        shot.setIsMarkedAsNice(niceShotRepository.isMarked(shot.getIdShot()));
        shot.setProfileHidden(shotEntity.getProfileHidden());
        shot.setMetadata(metadataMapper.metadataFromEntity(shotEntity));
        shot.setReplyCount(shotEntity.getReplyCount());
        shot.setViews(shotEntity.getViews());
        shot.setLinkClicks(shotEntity.getLinkClicks());
        shot.setReshootCount(shotEntity.getReshootCounter());
        shot.setCtaButtonLink(shotEntity.getCtaButtonLink());
        shot.setCtaButtonText(shotEntity.getCtaButtonText());
        shot.setCtaCaption(shotEntity.getCtaCaption());
        shot.setPromoted(shotEntity.getPromoted());
        return shot;
    }

    public List<Shot> transform(List<ShotEntity> shotEntities) {
        List<Shot> shots = new ArrayList<>(shotEntities.size());
        for (ShotEntity shotEntity : shotEntities) {
            Shot shot = transform(shotEntity);
            if (shot != null) {
                shots.add(shot);
            }
        }
        return shots;
    }

    public ShotEntity transform(Shot shot) {
        if (shot == null) {
            throw new IllegalArgumentException("Shot can't be null");
        }
        ShotEntity shotEntity = new ShotEntity();
        String idShot = shot.getIdShot();
        shotEntity.setIdShot(idShot);
        shotEntity.setComment(shot.getComment());
        shotEntity.setImage(shot.getImage());
        shotEntity.setImageWidth(shot.getImageWidth());
        shotEntity.setImageHeight(shot.getImageHeight());
        shotEntity.setType(shot.getType());
        String idUser = shot.getUserInfo().getIdUser();
        String username = shot.getUserInfo().getUsername();
        String avatar = shot.getUserInfo().getAvatar();
        Long verifiedUser = shot.getUserInfo().getVerifiedUser();
        shotEntity.setIdUser(idUser);
        shotEntity.setUsername(username);
        shotEntity.setUserPhoto(avatar);
        shotEntity.setVerifiedUser(verifiedUser);
        Shot.ShotStreamInfo eventInfo = shot.getStreamInfo();
        if (eventInfo != null) {
            shotEntity.setIdStream(eventInfo.getIdStream());
            shotEntity.setStreamTitle(eventInfo.getStreamTitle());
        }
        shotEntity.setIdShotParent(shot.getParentShotId());
        shotEntity.setIdUserParent(shot.getParentShotUserId());
        shotEntity.setUserNameParent(shot.getParentShotUsername());
        shotEntity.setUsername(shot.getUserInfo().getUsername());
        shotEntity.setBirth(shot.getPublishDate());
        shotEntity.setVideoUrl(shot.getVideoUrl());
        shotEntity.setVideoTitle(shot.getVideoTitle());
        shotEntity.setVideoDuration(shot.getVideoDuration());
        shotEntity.setNiceCount(shot.getNiceCount());
        shotEntity.setProfileHidden(shot.getProfileHidden());
        shotEntity.setReplyCount(shot.getReplyCount());
        shotEntity.setViews(shot.getViews());
        shotEntity.setLinkClicks((shot.getLinkClicks()));
        shotEntity.setReshootCounter(shot.getReshootCount());
        shotEntity.setCtaButtonLink(shot.getCtaButtonLink());
        shotEntity.setCtaButtonText(shot.getCtaButtonText());
        shotEntity.setCtaCaption(shot.getCtaCaption());
        shotEntity.setPromoted(shot.getPromoted());
        shotEntity.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
        metadataMapper.fillEntityWithMetadata(shotEntity, shot.getMetadata());
        return shotEntity;
    }

    public ShotDetail transform(ShotDetailEntity shotDetailEntity) {
        if (shotDetailEntity == null) {
            return null;
        }
        ShotDetail shotDetail = new ShotDetail();

        shotDetail.setShot(transform(shotDetailEntity.getShot()));
        shotDetail.setParentShot(transform(shotDetailEntity.getParentShot()));
        shotDetail.setReplies(transform(shotDetailEntity.getReplies()));
        if (shotDetailEntity.getParents() != null) {
            shotDetail.setParents(transform(shotDetailEntity.getParents()));
        } else {
            shotDetail.setParents(Collections.<Shot>emptyList());
        }
        return shotDetail;
    }

    public List<ShotEntity> transformInEntities(List<Shot> shots) {
        List<ShotEntity> shotEntities = new ArrayList<>(shots.size());
        for (Shot shot : shots) {
            shotEntities.add(transform(shot));
        }
        return shotEntities;
    }
}
