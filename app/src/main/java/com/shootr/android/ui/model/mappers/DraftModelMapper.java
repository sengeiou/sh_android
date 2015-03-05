package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.QueuedShot;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.User;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.DraftModel;
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
