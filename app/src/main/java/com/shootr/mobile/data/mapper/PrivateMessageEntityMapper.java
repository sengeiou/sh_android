package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PrivateMessageEntityMapper {

    private final MetadataMapper metadataMapper;

    @Inject public PrivateMessageEntityMapper(MetadataMapper metadataMapper) {
        this.metadataMapper = metadataMapper;
    }

    public PrivateMessage transform(PrivateMessageEntity privateMessageEntity) {
        if (privateMessageEntity == null) {
            return null;
        }
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setIdPrivateMessage(privateMessageEntity.getIdPrivateMessage());
        privateMessage.setComment(privateMessageEntity.getComment());
        privateMessage.setImage(privateMessageEntity.getImage());
        privateMessage.setImageHeight(privateMessageEntity.getImageHeight());
        privateMessage.setImageWidth(privateMessageEntity.getImageWidth());
        privateMessage.setPublishDate(privateMessageEntity.getBirth());
        privateMessage.setIdPrivateMessageChannel(privateMessageEntity.getIdPrivateMessageChannel());

        BaseMessage.BaseMessageUserInfo userInfo = new BaseMessage.BaseMessageUserInfo();
        userInfo.setIdUser(privateMessageEntity.getIdUser());
        userInfo.setUsername(privateMessageEntity.getUsername());
        userInfo.setVerifiedUser(privateMessageEntity.getVerifiedUser());
        privateMessage.setUserInfo(userInfo);
        privateMessage.setVideoUrl(privateMessageEntity.getVideoUrl());
        privateMessage.setVideoTitle(privateMessageEntity.getVideoTitle());
        privateMessage.setVideoDuration(privateMessageEntity.getVideoDuration());
        privateMessage.setMetadata(metadataMapper.metadataFromEntity(privateMessageEntity));
        return privateMessage;
    }

    public List<PrivateMessage> transform(List<PrivateMessageEntity> privateMessageEntities) {
        List<PrivateMessage> privateMessages = new ArrayList<>(privateMessageEntities.size());
        for (PrivateMessageEntity privateMessageEntity : privateMessageEntities) {
            PrivateMessage privateMessage = transform(privateMessageEntity);
            if (privateMessage != null) {
                privateMessages.add(privateMessage);
            }
        }
        return privateMessages;
    }

    public PrivateMessageEntity transform(PrivateMessage privateMessage) {
        if (privateMessage == null) {
            throw new IllegalArgumentException("PrivateMessage can't be null");
        }
        PrivateMessageEntity privateMessageEntity = new PrivateMessageEntity();
        privateMessageEntity.setIdPrivateMessage(privateMessage.getIdPrivateMessage());
        privateMessageEntity.setComment(privateMessage.getComment());
        privateMessageEntity.setImage(privateMessage.getImage());
        privateMessageEntity.setImageWidth(privateMessage.getImageWidth());
        privateMessageEntity.setImageHeight(privateMessage.getImageHeight());
        String idUser = privateMessage.getUserInfo().getIdUser();
        String username = privateMessage.getUserInfo().getUsername();
        String avatar = privateMessage.getUserInfo().getAvatar();
        Long verifiedUser = privateMessage.getUserInfo().getVerifiedUser();
        privateMessageEntity.setIdUser(idUser);
        privateMessageEntity.setUsername(username);
        privateMessageEntity.setVerifiedUser(verifiedUser);

        if (privateMessage.getIdTargetUser() != null) {
            privateMessageEntity.setIdTargetUser(privateMessage.getIdTargetUser());
        }
        privateMessageEntity.setUsername(privateMessage.getUserInfo().getUsername());
        privateMessageEntity.setBirth(privateMessage.getPublishDate());
        privateMessageEntity.setVideoUrl(privateMessage.getVideoUrl());
        privateMessageEntity.setVideoTitle(privateMessage.getVideoTitle());
        privateMessageEntity.setVideoDuration(privateMessage.getVideoDuration());
        privateMessageEntity.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
        metadataMapper.fillEntityWithMetadata(privateMessageEntity, privateMessage.getMetadata());
        return privateMessageEntity;
    }

    public List<PrivateMessageEntity> transformInEntities(List<PrivateMessage> privateMessages) {
        List<PrivateMessageEntity> privateMessageEntities = new ArrayList<>(privateMessages.size());
        for (PrivateMessage privateMessage : privateMessages) {
            privateMessageEntities.add(transform(privateMessage));
        }
        return privateMessageEntities;
    }
}