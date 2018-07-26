package com.shootr.mobile.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import com.shootr.mobile.ui.model.BackgroundModel;
import com.shootr.mobile.ui.model.ShotModel;

public class PromotedColorManager {

  private final String DEFAULT_COLOR_TEXT = "#FF000000";
  private final String MY_MESSAGE_COLOR_TEXT = "#ffffff";
  public static final String DETAIL_COLOR_BLACK = "#000000";
  public static final String DETAIL_COLOR_WHITE = "#ffffff";

  private Context context;

  public PromotedColorManager(Context context) {
    this.context = context;
  }

  public Drawable getDrawableForPromoted(ShotModel shot) {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setCornerRadius(convertDpToPx(15));

    if (shot.getEntitiesModel() != null && shot.getEntitiesModel().getPromoted() != null) {

      try {
        BackgroundModel background = shot.getEntitiesModel().getPromoted().getBackground();

        int[] colors = new int[2];

        if (background != null && background.getColors() != null) {
          colors[0] =
              Color.parseColor(PromotedColor.valueOf(background.getColors().get(0)).getColor());
          colors[1] =
              Color.parseColor(PromotedColor.valueOf(background.getColors().get(1)).getColor());

          drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

          drawable.setColors(colors);
        }
      } catch (Exception e) {
        setupDefaultBackground(shot, drawable);
      }

    } else {
      setupDefaultBackground(shot, drawable);
    }

    return drawable;
  }

  private void setupDefaultBackground(ShotModel shot, GradientDrawable drawable) {
    if (shot.isMine()) {
      drawable.setColor(Color.parseColor("#FF1A1A1A"));
    } else {

      drawable.setColor(Color.parseColor("#F5F5F5"));
    }
  }

  public int getDetailColors(ShotModel shotModel) {
    if (shotModel.getEntitiesModel().getPromoted() != null) {
      BackgroundModel background = shotModel.getEntitiesModel().getPromoted().getBackground();
      if (background != null && background.getColors() != null) {
        return Color.parseColor(
            PromotedColor.valueOf(background.getColors().get(0)).getDetailColors());
      }
    }
    return Color.parseColor(shotModel.isMine() ? MY_MESSAGE_COLOR_TEXT : DEFAULT_COLOR_TEXT);
  }

  public String getPromotedPrice(ShotModel shotModel) {
    String price = "";
    if (shotModel.getEntitiesModel().getPromoted() != null) {
      if (shotModel.getEntitiesModel().getPromoted().getCurrency() != null) {
        price = (int) shotModel.getEntitiesModel().getPromoted().getDisplayPrice()
            + CurrencySymbol.getSymbol(shotModel.getEntitiesModel().getPromoted().getCurrency());
      }
    }
    return price;
  }

  public int getPriceColor(ShotModel shotModel) {
    if (shotModel.getEntitiesModel().getPromoted() != null) {
      BackgroundModel background = shotModel.getEntitiesModel().getPromoted().getBackground();
      if (background != null && background.getColors() != null) {
        return Color.parseColor(PromotedColor.valueOf(background.getColors().get(0)).getColor());
      }
    }
    return Color.parseColor(MY_MESSAGE_COLOR_TEXT);
  }

  private int convertDpToPx(int dp) {
    return Math.round(
        dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
  }

  public Drawable getBackgroundDrawable(BackgroundModel background) {
    GradientDrawable drawable = new GradientDrawable();

    try {

      int[] colors = new int[2];

      if (background != null && background.getColors() != null) {
        colors[0] =
            Color.parseColor(PromotedColor.valueOf(background.getColors().get(0)).getColor());
        colors[1] =
            Color.parseColor(PromotedColor.valueOf(background.getColors().get(1)).getColor());

        drawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        drawable.setColors(colors);
      }
    } catch (Exception e) {
      /* no-op */
    }

    return drawable;
  }

  public int getDetailColors(BackgroundModel background) {
    if (background != null && background.getColors() != null) {
      return Color.parseColor(
          PromotedColor.valueOf(background.getColors().get(0)).getDetailColors());
    }
    return Color.parseColor(DEFAULT_COLOR_TEXT);
  }

  public Drawable getBackgroundForPromotedMark(ShotModel shotModel) {

    GradientDrawable backgroundDrawable = (GradientDrawable) getDrawableForPromoted(shotModel);

    backgroundDrawable.setCornerRadius(convertDpToPx(10));

    return backgroundDrawable;
  }

}
