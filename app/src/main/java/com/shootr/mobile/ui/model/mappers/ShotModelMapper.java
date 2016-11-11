package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.ui.model.ShotImageModel;
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
        ShotImageModel shotImageModel = new ShotImageModel();
        if (shot.getImage() != null) {
            shotImageModel.setImageUrl(shot.getImage());
            shotImageModel.setImageHeight(shot.getImageHeight());
            shotImageModel.setImageWidth(shot.getImageWidth());
        }
        shotModel.setImage(shotImageModel);
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
        shotModel.setLinkClickCount(shot.getLinkClicks() == null ? 0 : shot.getLinkClicks());
        shotModel.setViews(shot.getViews() == null ? 0 : shot.getViews());
        shotModel.setReshootCount(shot.getReshootCount() == null ? 0 : shot.getReshootCount());
        shotModel.setCtaButtonLink(shot.getCtaButtonLink());
        shotModel.setCtaButtonText(shot.getCtaButtonText());
        shotModel.setCtaCaption(shot.getCtaCaption());
        shotModel.setPromoted(shot.getPromoted());
        shotModel.setType(shot.getType());
        if (shot.getVerifiedUser() != null) {
            shotModel.setVerifiedUser(shot.getVerifiedUser() == 1);
        } else {
            shotModel.setVerifiedUser(false);
        }
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
