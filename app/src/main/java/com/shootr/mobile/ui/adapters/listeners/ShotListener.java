package com.shootr.mobile.ui.adapters.listeners;

import android.view.View;
import com.shootr.mobile.ui.model.BaseMessageModel;
import com.shootr.mobile.ui.model.ShotModel;

/**
 * Created by miniserver on 28/9/17.
 */

public interface ShotListener {

  void onAvatarClick(String userId, View avatarView);

  void onVideoClick(String url);

  void markNice(ShotModel shot);

  void unmarkNice(String idShot);

  void onUsernameClick(String username);

  void openMenu(ShotModel shot);

  void onShotClick(ShotModel shot);

  void onShotLongClick(ShotModel shot);

  void onImageClick(View sharedImage, BaseMessageModel shot);

  void onReshootClick(ShotModel shot);

  void onUndoReshootClick(ShotModel shot);

}
