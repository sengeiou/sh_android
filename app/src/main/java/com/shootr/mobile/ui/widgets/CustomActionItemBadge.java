package com.shootr.mobile.ui.widgets;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.shootr.mobile.R;

public class CustomActionItemBadge extends ActionItemBadge {

  public static void update(Activity act, MenuItem menu, Drawable icon, boolean following,
      Integer badgeCount) {
    BadgeStyle badgeStyle = new BadgeStyle(BadgeStyle.Style.DEFAULT,
        com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge,
        Color.parseColor("#01579b"), Color.parseColor("#01579b"), Color.WHITE);
    BadgeStyle followingBadgeStyle = new BadgeStyle(BadgeStyle.Style.DEFAULT,
        com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge,
        Color.parseColor("#FF4444"), Color.parseColor("#FF4444"), Color.WHITE);
    BadgeStyle noBadge = new BadgeStyle(BadgeStyle.Style.DEFAULT,
        R.layout.message_following_badge,
        Color.parseColor("#FF4444"), Color.parseColor("#FF4444"), Color.WHITE);
    if (badgeCount != null) {
      update(act, menu, icon, (following ? followingBadgeStyle : badgeStyle), badgeCount);
    } else {
      update(act, menu, icon, noBadge, null);
    }


  }

  public static void updateFilterAlert(Activity act, MenuItem menu, Drawable icon,
      String badgeCount) {
    BadgeStyle followingBadgeStyle = new BadgeStyle(BadgeStyle.Style.DEFAULT,
        R.layout.filter_badge,
        Color.parseColor("#f44336"), Color.parseColor("#f44336"), Color.WHITE);
    update(act, menu, icon, followingBadgeStyle, badgeCount);
  }

}
