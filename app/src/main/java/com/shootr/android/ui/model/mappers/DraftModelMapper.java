package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.ShotModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class DraftModelMapper {

    private final SessionRepository sessionRepository;
    private final ShotModelMapper shotModelMapper;

    @Inject public DraftModelMapper(SessionRepository sessionRepository, ShotModelMapper shotModelMapper) {
        this.sessionRepository = sessionRepository;
        this.shotModelMapper = shotModelMapper;
    }

    public ShotModel transform(QueuedShot draft) {
        Shot shot = draft.getShot();

        User currentUser = sessionRepository.getCurrentUser();
        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(currentUser.getIdUser());
        userInfo.setUsername(currentUser.getUsername());
        userInfo.setAvatar(currentUser.getPhoto());

        shot.setUserInfo(userInfo);
        return shotModelMapper.transform(shot);
    }

    public List<ShotModel> transform(List<QueuedShot> drafts) {
        List<ShotModel> shotModels = new ArrayList<>();
        for (QueuedShot draft : drafts) {
            shotModels.add(transform(draft));
        }
        return shotModels;
    }
}
