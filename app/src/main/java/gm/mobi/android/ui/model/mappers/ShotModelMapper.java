package gm.mobi.android.ui.model.mappers;


import gm.mobi.android.db.objects.ShotEntity;
import gm.mobi.android.db.objects.UserEntity;
import gm.mobi.android.ui.model.ShotModel;

public class ShotModelMapper {


    public ShotModel toShotModel(UserEntity user,ShotEntity shot){
        ShotModel shotModel = new ShotModel();
        shotModel.setPhoto(user.getPhoto());
        shotModel.setIdUser(user.getIdUser());
        shotModel.setComment(shot.getComment());
        shotModel.setIdShot(shot.getIdShot());
        shotModel.setUseName(user.getUserName());
        shotModel.setCsys_birth(shot.getCsys_birth());
        return shotModel;
    }


}
