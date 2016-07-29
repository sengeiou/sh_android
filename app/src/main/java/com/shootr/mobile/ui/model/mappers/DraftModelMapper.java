package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.DraftModel;
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

    public DraftModel transform(QueuedShot draft) {
        DraftModel draftModel = new DraftModel();
        draftModel.setIdQueue(draft.getIdQueue());
        draftModel.setImageFile(draft.getImageFile());

        Shot shot = draft.getShot();

        setCurrentUserInShot(shot);
        draftModel.setShotModel(shotModelMapper.transform(shot));

        return draftModel;
    }

    public List<DraftModel> transform(List<QueuedShot> drafts) {
        List<DraftModel> draftModels = new ArrayList<>();
        for (QueuedShot draft : drafts) {
            draftModels.add(transform(draft));
        }
        return draftModels;
    }

    private void setCurrentUserInShot(Shot shot) {
        User currentUser = sessionRepository.getCurrentUser();
        Shot.ShotUserInfo userInfo = new Shot.ShotUserInfo();
        userInfo.setIdUser(currentUser.getIdUser());
        userInfo.setUsername(currentUser.getUsername());
        userInfo.setAvatar(currentUser.getPhoto());
        shot.setUserInfo(userInfo);
    }
}
