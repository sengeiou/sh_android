package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.ShotModel;
import java.util.List;

public interface ShotDetailView  {

    void renderShot(ShotModel shotModel);

    void renderReplies(List<ShotModel> shotModles);

    void openImage(String imageUrl);

    void openProfile(String idUser);

    void setReplyUsername(String username);

    void scrollToBottom();

    void renderParent(ShotModel parentShot);

    void startProfileContainerActivity(String username);

    void showError(String errorMessage);

    void showShotShared();
}