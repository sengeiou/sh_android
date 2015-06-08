package com.shootr.android.ui.views;

import com.shootr.android.ui.model.ShotModel;
import java.util.List;

public interface ShotDetailView  {

    void renderShot(ShotModel shotModel);

    void renderReplies(List<ShotModel> shotModles);

    void openImage(String imageUrl);

    void openProfile(String idUser);

    void setReplyUsername(String username);

    void hideNewReply();

    void scrollToBottom();

    void renderParent(ShotModel parentShot);

    void showUserNotFoundNotification();

    void startProfileContainerActivity(String username);

}
