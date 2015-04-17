package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ShotModel;
import java.util.List;

public interface ShotDetailView  {

    void renderShot(ShotModel shotModel);

    void renderReplies(List<ShotModel> shotModles);

    void openImage(String imageUrl);

    void openProfile(Long idUser);

    void setReplyUsername(String username);

    void scrollToBottom();

    void renderParent(ShotModel parentShot);
}
