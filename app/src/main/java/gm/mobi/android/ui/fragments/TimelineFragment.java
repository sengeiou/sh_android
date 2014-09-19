package gm.mobi.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.ui.adapters.TimelineAdapter;
import gm.mobi.android.ui.base.BaseFragment;
import gm.mobi.android.ui.widgets.ListViewScrollObserver;
import gm.mobi.android.ui.widgets.ScrollListView;
import hugo.weaving.DebugLog;
import timber.log.Timber;

public class TimelineFragment extends BaseFragment {

    @Inject Picasso picasso;

    @InjectView(R.id.timeline_list) ListView listView;
    @InjectView(R.id.timeline_imwatching) View imwatchingView;
    @InjectView(R.id.timeline_new) View newShotView;

    private TimelineAdapter adapter;
    private View.OnClickListener avatarClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        avatarClickListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                int position = ((TimelineAdapter.ViewHolder) v.getTag()).position;
                openProfile(position);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        try {
            getActivity().getActionBar().setTitle("Timeline");
        } catch (NullPointerException e) {
            Timber.w("Activity null in TimelineFragment#onViewCreated()");
        }

        // List scroll stuff
        new ListViewScrollObserver(listView).setOnScrollUpAndDownListener(new ListViewScrollObserver.OnListViewScrollListener() {
            @Override
            public void onScrollUpDownChanged(int delta, int scrollPosition, boolean exact) {
                // delta negativo: scoll abajo
                if (delta < 0) {
                    newShotView.setVisibility(View.GONE);
                } else if (delta > 10) {
                    newShotView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollIdle() {

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GolesApplication.get(getActivity()).inject(this);
        adapter = new TimelineAdapter(getActivity(), picasso, avatarClickListener);
        listView.setAdapter(adapter);
    }


    @OnItemClick(R.id.timeline_list)
    public void openShot(int position) {
        //TODO Shot detail
        TimelineAdapter.MockShot shot = adapter.getItem(position);
        Toast.makeText(getActivity(), "Shot " + position + " from " + shot.name, Toast.LENGTH_SHORT).show();
        Timber.d("Clicked shot %d from %s", position, shot.name);
    }


    public void openProfile(int position) {
        TimelineAdapter.MockShot shot = adapter.getItem(position);
        //TODO profile
        Toast.makeText(getActivity(), "Open " + shot.name + "'s profile", Toast.LENGTH_SHORT).show();
        Timber.d("Open profile in position %d: %s", position, shot.name);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timeline, menu);
        // Little hack for ActionBarCompat
        menu.findItem(R.id.menu_info).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
}

