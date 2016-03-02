package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.StreamPercentageUtils;
import javax.inject.Inject;

public class StreamDataInfoActivity extends BaseToolbarDecoratedActivity {

    public static final String ARGUMENT_PARTICIPANTS_NUMBER = "participantsNumber";
    public static final String ARGUMENT_SHOTS_NUMBER = "shotsNumber";
    public static final String ARGUMENT_FAVORITES_NUMBER = "favoritesNumber";
    public static final String ARGUMENT_UNIQUE_SHOTS = "uniqueShotsNumber";

    @Bind(R.id.stream_data_info_participants_number) TextView participantsNumberTextView;
    @Bind(R.id.stream_data_info_shots_number) TextView shotsNumberTextView;
    @Bind(R.id.stream_data_info_favorites_number) TextView favoritesNumberTextView;
    @Bind(R.id.stream_data_info_favorites_number_pct) TextView favoritesNumberPtcTextView;
    @Bind(R.id.stream_data_info_participants_with_shots_number) TextView participantsWithShotsNumberTextView;
    @Bind(R.id.stream_data_info_participants_with_shots_number_pct) TextView participantsWithShotsPtcNumberTextView;
    @BindString(R.string.analytics_screen_stream_numbers) String analyticsScreenStreamNumbers;

    @Inject AnalyticsTool analyticsTool;
    @Inject StreamPercentageUtils streamPercentageUtils;

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_stream_data_info;
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        analyticsTool.analyticsStart(getBaseContext(), analyticsScreenStreamNumbers);

        setupStatics();
    }

    private void setupStatics() {
        Intent intent = getIntent();
        Long participantsNumber = (Long) intent.getExtras().get(ARGUMENT_PARTICIPANTS_NUMBER);
        Integer favoritesNumber = intent.getExtras().getInt(ARGUMENT_FAVORITES_NUMBER);
        Long shotsNumber = (Long) intent.getExtras().get(ARGUMENT_SHOTS_NUMBER);
        Long participantsWithShotsNumber = (Long) intent.getExtras().get(ARGUMENT_UNIQUE_SHOTS);
        Double pctParticipantsWithShots = streamPercentageUtils.getPercentage(participantsWithShotsNumber,participantsNumber);
        Double pctFavoritesNumber = streamPercentageUtils.getPercentage(favoritesNumber.longValue(),participantsNumber);

        participantsNumberTextView.setText(String.valueOf(participantsNumber));
        favoritesNumberTextView.setText(String.valueOf(favoritesNumber));
        shotsNumberTextView.setText(String.valueOf(shotsNumber));
        participantsWithShotsNumberTextView.setText(String.valueOf(participantsWithShotsNumber));
        participantsWithShotsPtcNumberTextView.setText(getString(R.string.stream_data_info_pct,
          streamPercentageUtils.formatPercentage(pctParticipantsWithShots)));
        favoritesNumberPtcTextView.setText(getString(R.string.stream_data_info_pct,
          streamPercentageUtils.formatPercentage(pctFavoritesNumber)));
    }

    @Override protected void initializePresenter() {
        /* no-op */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override protected void onPause() {
        super.onPause();
        analyticsTool.analyticsStop(getBaseContext(), this);
    }
}
