package com.shootr.android.ui.model.mappers;


import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.mapper.UserAvatarUrlBuilder;
import com.shootr.android.ui.model.ShotModel;
import javax.inject.Inject;

@Deprecated
public class ShotEntityModelMapper {

    private final UserAvatarUrlBuilder userAvatarUrlBuilder;

    @Inject public ShotEntityModelMapper(UserAvatarUrlBuilder userAvatarUrlBuilder) {
        this.userAvatarUrlBuilder = userAvatarUrlBuilder;
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
        return shotModel;
    }

    public ShotModel toShotModelFromShot(ShotEntity shot){
        ShotModel shotModel = new ShotModel();
        String userPhotoUrl = userAvatarUrlBuilder.calculateUserPhotoUrlFromIdUser(shot.getIdUser());
        shotModel.setPhoto(userPhotoUrl);
        shotModel.setIdUser(shot.getIdUser());
        shotModel.setComment(shot.getComment());
        shotModel.setImage(shot.getImage());
        shotModel.setEventTag(shot.getEventTag());
        shotModel.setEventTitle(shot.getEventTitle());
        shotModel.setIdShot(shot.getIdShot());
        shotModel.setUsername(shot.getUsername());
        shotModel.setType(shot.getType());
        shotModel.setCsysBirth(shot.getCsysBirth());
        shotModel.setReplyUsername(shot.getUserNameParent());
        shotModel.setParentShotId(shot.getIdShotParent());
        return shotModel;
    }


}
