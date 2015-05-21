package com.shootr.android.ui.model.mappers;


import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.ui.model.ShotModel;
import javax.inject.Inject;

@Deprecated
public class ShotEntityModelMapper {

    @Inject public ShotEntityModelMapper() {
    }

    public ShotModel toShotModel(UserEntity user,ShotEntity shot){
        ShotModel shotModel = new ShotModel();
        shotModel.setPhoto(user.getPhoto());
        shotModel.setIdUser(user.getIdUser());
        shotModel.setComment(shot.getComment());
        shotModel.setImage(shot.getImage());
        shotModel.setEventTag(shot.getEventTag());
        shotModel.setEventTitle(shot.getEventTitle());
        shotModel.setIdShot(shot.getIdShot());
        shotModel.setUsername(user.getUserName());
        shotModel.setType(shot.getType());
        shotModel.setCsysBirth(shot.getCsysBirth());
        shotModel.setReplyUsername(shot.getUserNameParent());
        shotModel.setParentShotId(shot.getIdShotParent());
        shotModel.setVideoUrl(shot.getVideoUrl());
        shotModel.setVideoTitle(shot.getVideoTitle());
        shotModel.setVideoDuration(durationToText(shot.getVideoDuration()));
        return shotModel;
    }

    private String durationToText(Long durationInSeconds) {
        int minutes = (int) (durationInSeconds / 60);
        int seconds = (int) (durationInSeconds % 60);
        String secondsTwoDigits = seconds > 10 ? String.valueOf(seconds) : "0" + String.valueOf(seconds);
        return String.format("%d:%s", minutes, secondsTwoDigits);
    }


}
