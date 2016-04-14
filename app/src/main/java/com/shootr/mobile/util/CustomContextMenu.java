package com.shootr.mobile.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import com.shootr.mobile.domain.utils.Preconditions;
import java.util.ArrayList;
import java.util.List;

public class CustomContextMenu {

    private final Context context;
    private String[] titles;
    private Runnable[] callbacks;

    public CustomContextMenu(Context context, List<MenuAction> actions) {
        this.context = context;
        fillMenuActions(actions);
    }

    protected void fillMenuActions(List<MenuAction> actions) {
        titles = new String[actions.size()];
        callbacks = new Runnable[actions.size()];
        for (int i = 0; i < actions.size(); i++) {
            MenuAction action = actions.get(i);
            titles[i] = action.title;
            callbacks[i] = action.callback;
        }
    }

    public void show() {
        new AlertDialog.Builder(context).setItems(titles, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                callbacks[which].run();
            }
        }).show();
    }

    public static class Builder {

        private Context context;
        private final ArrayList<MenuAction> actions;

        public Builder(Context context) {
            this.context = context;
            this.actions = new ArrayList<>();
        }

        public Builder addAction(MenuAction menuAction) {
            actions.add(menuAction);
            return this;
        }

        public Builder addAction(@StringRes int titleRes, Runnable callback) {
            return addAction(new MenuAction(context.getString(titleRes), callback));
        }

        public CustomContextMenu build() {
            Preconditions.checkArgument(!actions.isEmpty(), "Can't create a menu without actions");
            return new CustomContextMenu(context, actions);
        }

        public void show() {
            build().show();
        }
    }

    public static class MenuAction {

        private String title;
        private Runnable callback;

        public MenuAction(String title, Runnable callback) {
            this.title = title;
            this.callback = callback;
        }
    }
}
