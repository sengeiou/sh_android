package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.ui.model.ShotModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotModelMapper {

    @Inject public ShotModelMapper() {
    }

    public ShotModel transform(Shot shot) {
        if (shot == null) {
            return null;
        }
        ShotModel shotModel = new ShotModel();
        shotModel.setIdShot(shot.getIdShot());
        shotModel.setComment(shot.getComment());
        shotModel.setImage(shot.getImage());
        shotModel.setBirth(shot.getPublishDate());
        Shot.ShotUserInfo userInfo = shot.getUserInfo();
        shotModel.setUsername(userInfo.getUsername());
        shotModel.setIdUser(userInfo.getIdUser());
        shotModel.setPhoto(userInfo.getAvatar());
        Shot.ShotStreamInfo streamInfo = shot.getStreamInfo();
        if (streamInfo != null) {
            shotModel.setStreamId(streamInfo.getIdStream());
            shotModel.setStreamTitle(streamInfo.getStreamTitle());
        }
        shotModel.setReplyUsername(shot.getParentShotUsername());
        shotModel.setParentShotId(shot.getParentShotId());
        shotModel.setVideoUrl(shot.getVideoUrl());
        shotModel.setVideoTitle(shot.getVideoTitle());
        shotModel.setVideoDuration(durationToText(shot.getVideoDuration()));
        shotModel.setNiceCount(shot.getNiceCount());
        shotModel.setIsMarkedAsNice(shot.isMarkedAsNice());
        shotModel.setHide(shot.getProfileHidden());
        shotModel.setReplyCount(shot.getReplyCount());
        return shotModel;
    }

    private String durationToText(Long durationInSeconds) {
        if (durationInSeconds == null) {
            return null;
        }
        int minutes = (int) (durationInSeconds / 60);
        int seconds = (int) (durationInSeconds % 60);
        String secondsTwoDigits = seconds > 10 ? String.valueOf(seconds) : "0" + String.valueOf(seconds);
        return String.format("%d:%s", minutes, secondsTwoDigits);
    }

    public List<ShotModel> transform(List<Shot> shots) {
        List<ShotModel> shotModels = new ArrayList<>();
        for (Shot shot : shots) {
            shotModels.add(transform(shot));
        }
        return shotModels;
    }
}
