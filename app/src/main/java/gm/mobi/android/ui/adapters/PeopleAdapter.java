package gm.mobi.android.ui.adapters;

import android.content.Context;
import com.squareup.picasso.Picasso;
import gm.mobi.android.db.objects.User;
import java.util.List;

public class PeopleAdapter extends UserListAdapter {

    public PeopleAdapter(Context context, Picasso picasso, List<User> users) {
        super(context, picasso, users);
    }

    @Override public boolean isFollowButtonVisible() {
        return false;
    }
}
