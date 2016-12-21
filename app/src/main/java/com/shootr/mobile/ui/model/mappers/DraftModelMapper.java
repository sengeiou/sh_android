package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import com.shootr.mobile.domain.model.shot.BaseMessage;
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
    private final PrivateMessageModelMapper privateMessageModelMapper;

    @Inject public DraftModelMapper(SessionRepository sessionRepository,
        ShotModelMapper shotModelMapper, PrivateMessageModelMapper privateMessageModelMapper) {
        this.sessionRepository = sessionRepository;
        this.shotModelMapper = shotModelMapper;
        this.privateMessageModelMapper = privateMessageModelMapper;
    }

    public DraftModel transformShot(QueuedShot draft) {
        DraftModel draftModel = new DraftModel();
        draftModel.setIdQueue(draft.getIdQueue());
        draftModel.setImageFile(draft.getImageFile());

        BaseMessage shot = draft.getBaseMessage();

        setCurrentUserInShot(shot);
        draftModel.setShotModel(shotModelMapper.transform((Shot) shot));

        return draftModel;
    }

    public DraftModel transformPrivateMessage(QueuedShot draft) {
        DraftModel draftModel = new DraftModel();
        draftModel.setIdQueue(draft.getIdQueue());
        draftModel.setImageFile(draft.getImageFile());

        BaseMessage shot = draft.getBaseMessage();

        setCurrentUserInShot(shot);
        draftModel.setShotModel(privateMessageModelMapper.transform((PrivateMessage) shot, ""));

        return draftModel;
    }

    public List<DraftModel> transformShot(List<QueuedShot> drafts) {
        List<DraftModel> draftModels = new ArrayList<>();
        for (QueuedShot draft : drafts) {
            draftModels.add(transformShot(draft));
        }
        return draftModels;
    }

    private void setCurrentUserInShot(BaseMessage shot) {
        User currentUser = sessionRepository.getCurrentUser();
        BaseMessage.BaseMessageUserInfo userInfo = new BaseMessage.BaseMessageUserInfo();
        userInfo.setIdUser(currentUser.getIdUser());
        userInfo.setUsername(currentUser.getUsername());
        userInfo.setAvatar(currentUser.getPhoto());
        shot.setUserInfo(userInfo);
    }
}
