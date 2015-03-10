package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.Shot;
import com.shootr.android.ui.model.ShotModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class ShotModelMapper {

    @Inject public ShotModelMapper() {
    }

    public ShotModel transform(Shot shot) {
        ShotModel shotModel = new ShotModel();
        shotModel.setIdShot(shot.getIdShot());
        shotModel.setComment(shot.getComment());
        shotModel.setImage(shot.getImage());
        shotModel.setCsysBirth(shot.getPublishDate());

        Shot.ShotUserInfo userInfo = shot.getUserInfo();
        shotModel.setUsername(userInfo.getUsername());
        shotModel.setIdUser(userInfo.getIdUser());
        shotModel.setPhoto(userInfo.getAvatar());


        Shot.ShotEventInfo eventInfo = shot.getEventInfo();
        if (eventInfo != null) {
            shotModel.setEventTag(eventInfo.getEventTag());
            shotModel.setEventTitle(eventInfo.getEventTitle());
        }
        return shotModel;
    }

    public List<ShotModel> transform(List<Shot> shots) {
        List<ShotModel> shotModels = new ArrayList<>();
        for (Shot shot : shots) {
            shotModels.add(transform(shot));
        }
        return shotModels;
    }
}
