package gm.mobi.android.ui.activities;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.squareup.picasso.Picasso;
import gm.mobi.android.R;
import gm.mobi.android.ui.adapters.InfoListAdapter;
import gm.mobi.android.ui.base.BaseSignedInActivity;
import gm.mobi.android.ui.model.MatchModel;
import gm.mobi.android.ui.model.UserWatchingModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class InfoActivity extends BaseSignedInActivity {

    @Inject Picasso picasso;

    @InjectView(R.id.info_items_list) ListView listView;
    InfoListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!restoreSessionOrLogin()) {
            // Stop execution if there is no user logged in
            return;
        }

        setContainerContent(R.layout.activity_info);

        ButterKnife.inject(this);

        adapter = new InfoListAdapter(this, picasso);
        adapter.setContent(getDummyInfo());
        listView.setAdapter(adapter);

    }

    @OnItemClick(R.id.info_items_list)
    public void onListItemClick(int position) {
        Toast.makeText(this, "Click: "+position, Toast.LENGTH_SHORT).show();
    }

    private Map<MatchModel, List<UserWatchingModel>> getDummyInfo() {
        ArrayMap<MatchModel, List<UserWatchingModel>> resMap = new ArrayMap<>();

        MatchModel match = new MatchModel();
        match.setTitle("Atlético-Barcelona");
        MatchModel match2 = new MatchModel();
        match2.setTitle("Sevilla-Manchester City");
        MatchModel match3 = new MatchModel();
        match3.setTitle("R.Madrid-La Palma del Condado");

        UserWatchingModel user = new UserWatchingModel();
        user.setName("Dummy");
        user.setStatus("Watching");
        user.setPhoto("http://www.pak101.com/funnypictures/Animals/2012/8/2/the_monopoly_cat_vbgkd_Pak101(dot)com.jpg");

        List<UserWatchingModel> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user);
        userList.add(user);

        resMap.put(match, userList);
        resMap.put(match2, userList);
        resMap.put(match3, userList);
        return resMap;
    }
}
