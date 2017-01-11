package com.shootr.mobile.ui.widgets;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;

public class CustomActionItemBadge extends ActionItemBadge {

  public static void update(Activity act, MenuItem menu, Drawable icon, boolean following,
      int badgeCount) {
    BadgeStyle badgeStyle = new BadgeStyle(BadgeStyle.Style.DEFAULT,
        com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge,
        Color.parseColor("#ffcc0000"), Color.parseColor("#ffcc0000"), Color.WHITE);
    BadgeStyle followingBadgeStyle = new BadgeStyle(BadgeStyle.Style.DEFAULT,
        com.mikepenz.actionitembadge.library.R.layout.menu_action_item_badge,
        Color.parseColor("#7f7f7f"), Color.parseColor("#7f7f7f"), Color.WHITE);
    update(act, menu, icon, (following ? followingBadgeStyle : badgeStyle), badgeCount);
  }
}
