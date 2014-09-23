package gm.mobi.android.ui.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import gm.mobi.android.R;

public class TimelineAdapter extends BindableAdapter<TimelineAdapter.MockShot> {

    List<MockShot> shots;
    private Picasso picasso;
    private final View.OnClickListener avatarClickListener;

    public TimelineAdapter(Context context, Picasso picasso, View.OnClickListener avatarClickListener) {
        super(context);
        this.picasso = picasso;
        this.avatarClickListener = avatarClickListener;
        //TODO datos reales
        shots = MockShot.getMockList();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return shots.size(); // White space and loading indicator
    }

    public boolean isLast(int position) {
        return position == getCount() - 1;
    }

    @Override
    public MockShot getItem(int position) {
        return shots.get(position); // Substract the margin item offset
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        View view = null;
        switch (getItemViewType(position)) {
            case 0: // Shot
                view = inflater.inflate(R.layout.item_list_shot, container, false);
                view.setTag(new ViewHolder(view, avatarClickListener));
                break;
        }
        return view;
    }

    @Override
    public void bindView(MockShot item, int position, View view) {
        switch (getItemViewType(position)) {
            case 0: // Shot
                ViewHolder vh = (ViewHolder) view.getTag();
                vh.position = position;

                vh.name.setText(item.name);
                vh.text.setText(item.text);
                vh.timestamp.setText(item.timestamp);
                picasso.load(item.avatar).into(vh.avatar);

                vh.avatar.setTag(vh);
                break;
        }
    }

    public void addShotsBelow(List<MockShot> newShots) {
        this.shots.addAll(newShots);
        notifyDataSetChanged();
    }

    public void addShotsAbove(List<MockShot> newShots) {
        this.shots.addAll(0, newShots);
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        @InjectView(R.id.shot_avatar) ImageView avatar;
        @InjectView(R.id.shot_user_name) TextView name;
        @InjectView(R.id.shot_timestamp) TextView timestamp;
        @InjectView(R.id.shot_text) TextView text;
        public int position;

        public ViewHolder(View view, View.OnClickListener avatarClickListener) {
            ButterKnife.inject(this, view);
            avatar.setOnClickListener(avatarClickListener);

        }
    }

    public static class MockShot {
        public String name;
        public String text;
        public String avatar;
        public String timestamp;

        public MockShot(String name, String text, String avatar, String timestamp) {
            this.name = name;
            this.text = text;
            this.avatar = avatar;
            this.timestamp = timestamp;
        }

        public static List<MockShot> getMockList() {
            List<MockShot> mockShots = Arrays.asList(
                    new MockShot("Ignasi", "Barcelona scores first", "https://s3.amazonaws.com/uifaces/faces/twitter/csswizardry/128.jpg", "3m"),
                    new MockShot("Victor", "He's such a nice person", "https://s3.amazonaws.com/uifaces/faces/twitter/idiot/128.jpg", "15m"),
                    new MockShot("Victor", "Who's the referee?", "https://s3.amazonaws.com/uifaces/faces/twitter/idiot/128.jpg", "22m"),
                    new MockShot("Christian", "Messi's goal!! https://www.youtube.com/watch?v=dQw4w9WgXcQx", "https://s3.amazonaws.com/uifaces/faces/twitter/peterlandt/128.jpg", "56m"),
                    new MockShot("Philip", "Nanananana batmannn", "https://s3.amazonaws.com/uifaces/faces/twitter/peterme/128.jpg", "58m"),

                    new MockShot("Ignasi", "He de reconocer que el lugar del evento es envidiable. De los pocos pabellones que sobrevivieron de la Expo 92. ", "https://s3.amazonaws.com/uifaces/faces/twitter/csswizardry/128.jpg", "3m"),
                    new MockShot("Victor", "Con la retrasada de Málaga que denuncio falsamente nos equivocamos\n" +
                            "Les pido perdón a ellos\n" +
                            "Y que ella no salga indemne \n" +
                            "Pero no es lo normal", "https://s3.amazonaws.com/uifaces/faces/twitter/idiot/128.jpg", "15m"),
                    new MockShot("Victor", "Insisto también en que el número de denuncias falsas por violación es ínfimo, pero eso no justifica la condena pública de todo denunciado.", "https://s3.amazonaws.com/uifaces/faces/twitter/idiot/128.jpg", "22m"),
                    new MockShot("Christian", "El mejor sistema para que la gente se olvide de las cosas es impedirle opinar sobre ellas ¿o era al revés?", "https://s3.amazonaws.com/uifaces/faces/twitter/peterlandt/128.jpg", "56m"),
                    new MockShot("Philip", "Media hora y todavía están saludandose los de la mesa. En serio, ¿Cuándo se van a dejar de hacer estas presentaciones?", "https://s3.amazonaws.com/uifaces/faces/twitter/peterme/128.jpg", "58m"),
                    new MockShot("Charles", "Looking at my Twitter timeline, I feel really sad for all this homeless guys sleeping under the rain near Apple Stores :( OH WAIT…", "https://s3.amazonaws.com/uifaces/faces/twitter/teleject/128.jpg", "1h"),

                    new MockShot("Victor", "He's an idiot", "https://s3.amazonaws.com/uifaces/faces/twitter/idiot/128.jpg", "15m"),
                    new MockShot("Victor", "Who's the referee?", "https://s3.amazonaws.com/uifaces/faces/twitter/idiot/128.jpg", "22m"),
                    new MockShot("Christian", "Messi's goal!! https://www.youtube.com/watch?v=dQw4w9WgXcQ", "https://s3.amazonaws.com/uifaces/faces/twitter/peterlandt/128.jpg", "56m"),
                    new MockShot("Philip", "Nanananana batmannn", "https://s3.amazonaws.com/uifaces/faces/twitter/peterme/128.jpg", "58m"),
                    new MockShot("Charles", "Barcelona scores last", "https://s3.amazonaws.com/uifaces/faces/twitter/teleject/128.jpg", "1h")
            );
            return new ArrayList<>(mockShots);

        }

        public static List<MockShot> getBlueList() {
            return Arrays.asList(
                    new MockShot("Blueman", "Blue shot", "https://s3.amazonaws.com/uifaces/faces/twitter/mrrocks/128.jpg", "2d"),
                    new MockShot("Blueman", "Blue shot", "https://s3.amazonaws.com/uifaces/faces/twitter/mrrocks/128.jpg", "2d"),
                    new MockShot("Blueman", "Blue shot", "https://s3.amazonaws.com/uifaces/faces/twitter/mrrocks/128.jpg", "2d")
            );
        }
    }

}
