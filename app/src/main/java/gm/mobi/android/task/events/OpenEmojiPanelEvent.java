package gm.mobi.android.task.events;


import android.view.View;

import gm.mobi.android.ui.adapters.PartidoAmigosAdapter;


public class OpenEmojiPanelEvent {



    private View rowView;
    private View buttonView;
    private Integer position;
    private PartidoAmigosAdapter.AmigoTmp amigoItem;

    public OpenEmojiPanelEvent(View row, View v, Integer position, PartidoAmigosAdapter.AmigoTmp amigo) {
        rowView = row;
        buttonView = v;
        this.position = position;
        amigoItem = amigo;
    }

    public View getButtonView() {
        return buttonView;
    }

    public Integer getPosition() {
        return position;
    }

    public View getRowView() {
        return rowView;
    }

    public PartidoAmigosAdapter.AmigoTmp getAmigoItem() {
        return amigoItem;
    }
}
