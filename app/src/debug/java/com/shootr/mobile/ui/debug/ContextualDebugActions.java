package com.shootr.mobile.ui.debug;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.shootr.mobile.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

public class ContextualDebugActions {

    public static abstract class DebugAction<T extends Activity> {

        private final Class<T> activityClass;

        protected DebugAction(Class<T> activityClass) {
            this.activityClass = activityClass;
        }

        /** Return true if action is applicable. Called each time the target view is added. */
        public boolean enabled() {
            return true;
        }

        /** Human-readable action name. Displayed in debug drawer. */
        public abstract String name();

        /** Perform this action using the specified activity. */
        public abstract void run(T activity);
    }

    private final Map<DebugAction<? extends Activity>, View> buttonMap;
    private final Map<Class<? extends Activity>, List<DebugAction<? extends Activity>>> actionMap;

    private final Context drawerContext;
    private final View contextualTitleView;
    private final LinearLayout contextualListView;

    private View.OnClickListener clickListener;

    public ContextualDebugActions(DebugAppContainer debugView, Collection<DebugAction<?>> debugActions) {
        buttonMap = new LinkedHashMap<>();
        actionMap = new LinkedHashMap<>();

        drawerContext = debugView.getContext();
        contextualTitleView = debugView.contextualTitleView;
        contextualListView = debugView.contextualListView;

        for (DebugAction<?> debugAction : debugActions) {
            Class cls = debugAction.activityClass;
            Timber.d("Adding %s action for %s.", debugAction.getClass().getSimpleName(), cls.getSimpleName());

            List<DebugAction<? extends Activity>> actionList = actionMap.get(cls);
            if (actionList == null) {
                actionList = new ArrayList<>(2);
                actionMap.put(cls, actionList);
            }
            actionList.add(debugAction);
        }
    }

    public void setActionClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void bindActions(Activity activity) {
        contextualListView.removeAllViews();
        List<DebugAction<? extends Activity>> actions = actionMap.get(activity.getClass());
        if (actions != null) {
            for (DebugAction<? extends Activity> action : actions) {
                if (!action.enabled()) {
                    continue;
                }
                Timber.d("Adding debug action \"%s\" for %s.", action.name(), activity.getClass().getSimpleName());

                View button = createButton(action, activity);
                buttonMap.put(action, button);
                contextualListView.addView(button);
            }
            updateContextualVisibility();
        }
    }

    private Button createButton(final DebugAction action, final Activity activity) {
        Button button = (Button) LayoutInflater.from(drawerContext)
          .inflate(R.layout.debug_drawer_contextual_action, contextualListView, false);
        button.setText(action.name());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onClick(view);
                }
                action.run(activity);
            }
        });
        return button;
    }

    private void updateContextualVisibility() {
        int visibility = contextualListView.getChildCount() > 0 ? View.VISIBLE : View.GONE;
        contextualTitleView.setVisibility(visibility);
        contextualListView.setVisibility(visibility);
    }
}
