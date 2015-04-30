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
        shotModel.setIdUser(user.getIdUser().toString());
        shotModel.setComment(shot.getComment());
        shotModel.setImage(shot.getImage());
        shotModel.setEventTag(shot.getEventTag());
        shotModel.setEventTitle(shot.getEventTitle());
        shotModel.setIdShot(shot.getIdShot().toString());
        shotModel.setUsername(user.getUserName());
        shotModel.setType(shot.getType());
        shotModel.setCsysBirth(shot.getCsysBirth());
        shotModel.setReplyUsername(shot.getUserNameParent());
        shotModel.setParentShotId(shot.getIdShotParent().toString());
        return shotModel;
    }


}
