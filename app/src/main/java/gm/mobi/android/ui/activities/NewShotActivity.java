package gm.mobi.android.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnTextChanged;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.ui.base.BaseActivity;
import gm.mobi.android.ui.base.BaseSignedInActivity;

public class NewShotActivity extends BaseSignedInActivity {

    public static final int MAX_LENGTH = 140;

    @InjectView(R.id.new_shot_avatar) ImageView avatar;
    @InjectView(R.id.new_shot_title) TextView name;
    @InjectView(R.id.new_shot_subtitle) TextView username;
    @InjectView(R.id.new_shot_text) TextView text;
    @InjectView(R.id.new_shot_char_counter) TextView charCounter;

    @Inject Picasso picasso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            return;
        }
        // Bypass layout inyection, translucent activity doesn't get along with Navigation Drawer
        setContentView(R.layout.activity_new_shot);

        ButterKnife.inject(this);

        //TODO desbestiar
        User user = GolesApplication.get(this).getCurrentUser();
        picasso.load(user.getPhoto()).into(avatar);
        name.setText(user.getName());
        username.setText("@" + user.getUserName());

        charCounter.setText(String.valueOf(MAX_LENGTH));
    }

    @OnTextChanged(R.id.new_shot_text)
    public void textChanged() {
        int textLength = text.length();
        int remainingLength = MAX_LENGTH - textLength;
        charCounter.setText(String.valueOf(remainingLength));
        if (remainingLength < 0) {
            //TODO setear estado negativo
        } else {
            //TODO setear positivo
        }
    }
}
