package com.shootr.android.ui.model.mappers;


import com.shootr.android.db.objects.ShotEntity;
import com.shootr.android.db.objects.UserEntity;
import com.shootr.android.ui.model.ShotModel;

public class ShotModelMapper {


    public ShotModel toShotModel(UserEntity user,ShotEntity shot){
        ShotModel shotModel = new ShotModel();
        shotModel.setPhoto(user.getPhoto());
        shotModel.setIdUser(user.getIdUser());
        shotModel.setComment(shot.getComment());
        shotModel.setIdShot(shot.getIdShot());
        shotModel.setUsername(user.getUserName());
        shotModel.setCsysBirth(shot.getCsysBirth());
        return shotModel;
    }


}
