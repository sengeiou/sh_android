package gm.mobi.android.ui.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import gm.mobi.android.R;
import gm.mobi.android.task.events.OpenEmojiPanelEvent;

public class PartidoAmigosAdapter extends BaseAdapter {

    @Inject Bus bus;
    private LayoutInflater layoutInflater;
    private Picasso picasso;
    private List<Object> items;
    private Resources resources;

    private int colorActive;
    private int colorInactive;

    private View.OnClickListener emojiClickListener;

    public PartidoAmigosAdapter(Context context, List<AmigoTmp> amigos) {
        layoutInflater = LayoutInflater.from(context);
        picasso = Picasso.with(context);
        resources = context.getResources();

        colorActive = resources.getColor(R.color.primary);
        colorInactive = resources.getColor(R.color.gray_60);
        fillItems(amigos);//TODO y los parámetros que hagan falta, perfil por ejemplo

        emojiClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer position = (Integer) v.getTag();
                View row = (View) v.getTag(R.id.tag_row);
                AmigoTmp amigo = (AmigoTmp) getItem(position);
                bus.post(new OpenEmojiPanelEvent(row, v, position, amigo));
            }
        };
    }

    private void fillItems(List<AmigoTmp> amigos){
        items = new ArrayList<Object>(amigos.size() + 3);
        // Me
        items.add(new PerfilTmp("https://lh5.googleusercontent.com/-0_-LYRsfJUc/UupgxbyQqnI/AAAAAAAAZqg/amp7dVZCIvY/s256-no/0bfc0765-d5e0-4633-bf4e-9570a50d607b"));
        // Separator
        items.add(new SeparatorKeyline2Item());
        // Friends
        items.add(new TitleKeyline2Item("Retar"));

        items.addAll(amigos);
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return items != null ? items.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = getItem(position);
        if (item instanceof TitleKeyline2Item) {
            return 0; // Title
        }else if( item instanceof SeparatorKeyline2Item) {
            return 1; // Separator
        }else if(item instanceof PerfilTmp) {
            return 2; // Me
        }else if(item instanceof AmigoTmp){
            return 3; // Amigo
        }
        else return -1; //TODO Error
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) > 1; // Disable 0 and 1
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case 0: //Title
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.item_list_title_keyline_2, parent, false);
                }
                TitleKeyline2Item titleItem = (TitleKeyline2Item) getItem(position);
                ((TextView)convertView).setText(titleItem.title);
                break;
            case 1: // Separator
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.separator_keyline_2, parent, false);
                }
                break;
            case 2: // Me
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.item_list_partido_me, parent, false);
                }
                PerfilTmp perfilItem = (PerfilTmp) getItem(position);
                ImageView avatarMe = ButterKnife.findById(convertView, R.id.item_list_partido_me_avatar);

                picasso.load(perfilItem.avatarUrl).noFade().into(avatarMe);
                setMeViewWatchingStatus(perfilItem, convertView);
                break;
            case 3: //Amigo
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.item_list_partido_amigos, parent, false);
                }
                AmigoTmp amigoItem = (AmigoTmp) getItem(position);
                TextView name = ButterKnife.findById(convertView, R.id.item_list_partido_amigos_name);
                TextView status = ButterKnife.findById(convertView, R.id.item_list_partido_amigos_status);
                ImageView avatar = ButterKnife.findById(convertView, R.id.item_list_partido_amigos_avatar);
                View emojiButton = ButterKnife.findById(convertView, R.id.item_list_partido_amigos_emoji);

                name.setText(amigoItem.name);
                status.setText(amigoItem.getStatusMessage());
                status.setTextColor(amigoItem.status == 0 ? colorInactive : colorActive);
                picasso.load(amigoItem.avatarUrl).noFade().into(avatar); // noFade for compatibility with CircularImageView
                emojiButton.setOnClickListener(emojiClickListener);
                emojiButton.setTag(position);
                emojiButton.setTag(R.id.tag_row, convertView);
                break;
        }
        return convertView;
    }

    public void setMeViewWatchingStatus(PerfilTmp perfil, View rootView) {
        TextView statusMe = ButterKnife.findById(rootView, R.id.item_list_partido_me_status);
        CheckBox checkMe = ButterKnife.findById(rootView, R.id.item_list_partido_me_checkbox);

        if (perfil.viendoPartido) {
            statusMe.setText("Estoy viendo el partido");
            statusMe.setTextColor(colorActive);
            checkMe.setChecked(true);
        } else {
            statusMe.setText("No lo estoy viendo");
            statusMe.setTextColor(colorInactive);
            checkMe.setChecked(false);
        }
    }


    /**
     * Clase temporal modelando Amigo para poder hacer el prototipo funcional del adapter.
     * Habrá que sustituír esta clase por la clase correcta del modelo una vez se defina.
     */
    public static class AmigoTmp{
        public String name;
        public String avatarUrl;
        public int status; //0: no sigue, 1: notificaciones, 2: viendo el partido

        public AmigoTmp(String name, String avatarUrl, int status) {
            this.name = name;
            this.avatarUrl = avatarUrl;
            this.status = status;
        }

        public String getStatusMessage() {
            switch (status) {
                case 0:
                    return "No sigue el partido";
                case 1:
                    return "Notificaciones";
                case 2:
                    return "Viendo el partido";
                default:
                    return ":S";
            }
        }

        public static List<AmigoTmp> getFakeList() {
            List<AmigoTmp> lista = new ArrayList<AmigoTmp>();
            lista.add(new AmigoTmp("Han solo", "http://www.grunfogamers.com/wp-content/uploads/2013/02/han-solo.jpg", 2));
            lista.add(new AmigoTmp("Lucky Luke", "http://img2.wikia.nocookie.net/__cb20091030151422/starwars/images/thumb/d/d9/Luke-rotjpromo.jpg/250px-Luke-rotjpromo.jpg", 2));
            lista.add(new AmigoTmp("Maestro", "http://www.naturalhealth365.com/images/soda.jpg", 1));
            lista.add(new AmigoTmp("Chuchi", "https://comunidaddelanilloverde.files.wordpress.com/2013/10/4445159chewaka.jpg", 1));
            lista.add(new AmigoTmp("Papá pitufo", "http://www.oracleyyo.com/media/blogs/oracleyyo/ArchivosBlog/Imagenes/darthVader.jpg", 1));
            lista.add(new AmigoTmp("Dolly", "http://www.starwarshelmets.com/2009/original-stunt-stormtrooper-helmet.jpg", 0));
            lista.add(new AmigoTmp("Dolly", "http://www.starwarshelmets.com/2009/original-stunt-stormtrooper-helmet.jpg", 0));
            lista.add(new AmigoTmp("Dolly", "http://www.starwarshelmets.com/2009/original-stunt-stormtrooper-helmet.jpg", 0));
            lista.add(new AmigoTmp("Dolly", "http://www.starwarshelmets.com/2009/original-stunt-stormtrooper-helmet.jpg", 0));
            lista.add(new AmigoTmp("Dolly", "http://www.starwarshelmets.com/2009/original-stunt-stormtrooper-helmet.jpg", 0));
            return lista;
        }
    }

    public static class PerfilTmp {
        public String avatarUrl;
        public boolean viendoPartido;

        public PerfilTmp(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

    }
    public static class SeparatorKeyline2Item {}

    public static class TitleKeyline2Item {
        public String title;

        public TitleKeyline2Item(String title) {
            this.title = title;
        }
    }
}
