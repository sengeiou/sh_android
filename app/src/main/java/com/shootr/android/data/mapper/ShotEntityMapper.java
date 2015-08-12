package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.entity.ShotDetailEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.domain.EntityMetadata;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.ShotDetail;
import com.shootr.android.domain.repository.NiceShotRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ShotEntityMapper {

    private final NiceShotRepository niceShotRepository;

    @Inject public ShotEntityMapper(NiceShotRepository niceShotRepository) {
        this.niceShotRepository = niceShotRepository;
    }

    public Shot transform(ShotEntity shotEntity) {
        if (shotEntity == null) {
            return null;
        }
        Shot shot = new Shot();
        shot.setIdShot(shotEntity.getIdShot());
        shot.setComment(shotEntity.getComment());
        shot.setImage(shotEntity.getImage());
        shot.setPublishDate(shotEntity.getBirth());
        if (shotEntity.getIdStream() != null) {
            Shot.ShotStreamInfo eventInfo = new Shot.ShotStreamInfo();
            eventInfo.setIdStream(shotEntity.getIdStream());
            eventInfo.setStreamTitle(shotEntity.getStreamTitle());
            eventInfo.setStreamTag(shotEntity.getStreamTag());
            shot.setStreamInfo(eventInfo);
        }

        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(shotEntity.getIdUser());
        userInfo.setUsername(shotEntity.getUsername());
        userInfo.setAvatar(shotEntity.getUserPhoto());
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

        shot.setMetadata(metadataFromEntity(shotEntity));

        return shot;
    }

    private EntityMetadata metadataFromEntity(Synchronized entity) {
        EntityMetadata metadata = new EntityMetadata();
        metadata.setBirth(entity.getBirth());
        metadata.setModified(entity.getModified());
        metadata.setDeleted(entity.getDeleted());
        metadata.setRevision(entity.getRevision());
        metadata.setSynchronizedStatus(entity.getSynchronizedStatus());
        return metadata;

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
        shotEntity.setType(shot.getType());
        String idUser = shot.getUserInfo().getIdUser();
        String username = shot.getUserInfo().getUsername();
        String avatar = shot.getUserInfo().getAvatar();
        shotEntity.setIdUser(idUser);
        shotEntity.setUsername(username);
        shotEntity.setUserPhoto(avatar);

        Shot.ShotStreamInfo eventInfo = shot.getStreamInfo();
        if (eventInfo != null) {
            shotEntity.setIdStream(eventInfo.getIdStream());
            shotEntity.setStreamTitle(eventInfo.getStreamTitle());
            shotEntity.setStreamTag(eventInfo.getStreamTag());
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

        shotEntity.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);

        metadataToEntity(shotEntity, shot.getMetadata());
        return shotEntity;
    }

    private void metadataToEntity(Synchronized entity, EntityMetadata metadata) {
        if (metadata != null) {
            entity.setBirth(metadata.getBirth());
            entity.setModified(metadata.getModified());
            entity.setDeleted(metadata.getDeleted());
            entity.setRevision(metadata.getRevision());
            entity.setSynchronizedStatus(metadata.getSynchronizedStatus());
        }
    }

    public ShotDetail transform(ShotDetailEntity shotDetailEntity) {
        if (shotDetailEntity == null) {
            return null;
        }
        ShotDetail shotDetail = new ShotDetail();

        shotDetail.setShot(transform(shotDetailEntity.getShot()));
        shotDetail.setParentShot(transform(shotDetailEntity.getParentShot()));
        shotDetail.setReplies(transform(shotDetailEntity.getReplies()));

        return shotDetail;
    }
}
