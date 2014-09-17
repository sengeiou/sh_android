package gm.mobi.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import gm.mobi.android.R;
import gm.mobi.android.ui.base.BaseSignedInActivity;

public class MainActivity extends BaseSignedInActivity {

    @Inject Picasso picasso;
    @InjectView(android.R.id.text1) TextView textView;
    @InjectView(android.R.id.icon) ImageView imageView;


    //TODO recibir parámetros para indicar si viene de registro, login o nueva
    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreSessionOrLogin()) {
            // Stop execution if there is no user logged in
            return;
        }

        setContainerContent(R.layout.main_activity);
        ButterKnife.inject(this);

        textView.setText("Bienvenido, " + getCurrentUser().getName());
        picasso.load(getCurrentUser().getPhoto()).into(imageView);
    }
}
