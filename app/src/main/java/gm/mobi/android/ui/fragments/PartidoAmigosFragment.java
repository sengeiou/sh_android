package gm.mobi.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import gm.mobi.android.R;
import gm.mobi.android.task.BusProvider;
import gm.mobi.android.task.events.OpenEmojiPanelEvent;
import gm.mobi.android.ui.adapters.PartidoAmigosAdapter;
import gm.mobi.android.ui.base.BaseFragment;

public class PartidoAmigosFragment extends BaseFragment {

    @InjectView(android.R.id.list) ListView mListView;
    @InjectView(R.id.partido_amigos_emoji_container) View mEmojiPanel;

    private PartidoAmigosAdapter mAdapter;
    private boolean isEmojiPanelOpen = false;

    //TODO añadir parámetros necesarios
    public static PartidoAmigosFragment newInstance() {
        return new PartidoAmigosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_partido_amigos, container, false);
        ButterKnife.inject(this, v);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new PartidoAmigosAdapter(getActivity(), PartidoAmigosAdapter.AmigoTmp.getFakeList());
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = mAdapter.getItem(position);
                if(item instanceof PartidoAmigosAdapter.PerfilTmp) {
                    PartidoAmigosAdapter.PerfilTmp perfil = (PartidoAmigosAdapter.PerfilTmp) item;

                    // Toggle
                    perfil.viendoPartido = !perfil.viendoPartido;

                    // Update view
                    mAdapter.setMeViewWatchingStatus(perfil, view);
                    setViendoPartido(perfil.viendoPartido);
                }
            }
        });
    }


    private void setViendoPartido(boolean viendoPartido) {
        // Hi :)
    }


    @Subscribe
    public void openEmojiPanel(OpenEmojiPanelEvent event) {
        isEmojiPanelOpen = true;
        View button = event.getButtonView();
        View row = event.getRowView();
        int panelHeight = mEmojiPanel.getHeight();
        int listTop = mListView.getTop();
        int listBottom = mListView.getBottom();
        int buttonTop = button.getTop();
        int buttonBottom = button.getBottom();
        int rowTop = row.getTop();
        int rowBottom = row.getBottom();

        //TODO login to decide where to place the panel
        Log.d("Emoji", "ButtonTop: " + buttonTop);
        Log.d("Emoji", "RowTop: " + rowTop);
        mEmojiPanel.setTranslationY(rowTop);
        mEmojiPanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }


}
