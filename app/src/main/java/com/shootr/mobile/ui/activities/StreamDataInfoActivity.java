package com.shootr.mobile.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.StreamPercentageUtils;
import javax.inject.Inject;

public class StreamDataInfoActivity extends BaseToolbarDecoratedActivity {

  public static final String EXTRA_STREAM = "stream";

  @BindView(R.id.stream_data_info_participants_number) TextView participantsNumberTextView;
  @BindView(R.id.stream_data_info_shots_number) TextView shotsNumberTextView;
  @BindView(R.id.stream_data_info_favorites_number) TextView favoritesNumberTextView;
  @BindView(R.id.stream_data_info_participants_with_shots_number) TextView
      participantsWithShotsNumberTextView;
  @BindView(R.id.stream_data_info_stream_name) TextView streamNameTextView;
  @BindView(R.id.stream_data_info_participants_with_shots_number_pct) TextView
      participantsWithShotsPtcNumberTextView;

  @BindString(R.string.analytics_screen_stream_numbers) String analyticsScreenStreamNumbers;

  @Inject AnalyticsTool analyticsTool;
  @Inject StreamPercentageUtils streamPercentageUtils;
  @Inject SessionRepository sessionRepository;

  @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        /* no-op */
  }

  @Override protected int getLayoutResource() {
    return R.layout.activity_stream_data_info;
  }

  @Override protected void initializeViews(Bundle savedInstanceState) {
    ButterKnife.bind(this);
    analyticsTool.analyticsStart(getBaseContext(), analyticsScreenStreamNumbers);
    AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
    builder.setContext(getBaseContext());
    builder.setActionId(analyticsScreenStreamNumbers);
    builder.setLabelId(analyticsScreenStreamNumbers);
    builder.setSource(analyticsScreenStreamNumbers);
    if (sessionRepository.getCurrentUser() != null) {
      builder.setUser(sessionRepository.getCurrentUser());
      builder.setIdStream(sessionRepository.getCurrentUser().getIdWatchingStream());
      builder.setStreamName(sessionRepository.getCurrentUser().getWatchingStreamTitle());
    }
    analyticsTool.analyticsSendAction(builder);
    setupStatics();
  }

  private void setupStatics() {
    Intent intent = getIntent();
    StreamModel streamModel = (StreamModel) intent.getSerializableExtra(EXTRA_STREAM);

    String streamName = streamModel.getTitle();
    Long participantsNumber =  streamModel.getHistoricWatchers();
    Integer favoritesNumber = streamModel.getTotalFollowers();
    Long shotsNumber = streamModel.getTotalShots();
    Long participantsWithShotsNumber = streamModel.getUniqueShots();
    Double pctParticipantsWithShots =
        streamPercentageUtils.getPercentage(participantsWithShotsNumber, participantsNumber);
    streamNameTextView.setText(streamName);
    participantsNumberTextView.setText(String.valueOf(participantsNumber));
    favoritesNumberTextView.setText(String.valueOf(favoritesNumber));
    shotsNumberTextView.setText(String.valueOf(shotsNumber));
    participantsWithShotsNumberTextView.setText(String.valueOf(participantsWithShotsNumber));
    participantsWithShotsPtcNumberTextView.setText(getString(R.string.stream_data_info_pct,
        streamPercentageUtils.formatPercentage(pctParticipantsWithShots)));
  }

  @Override protected void initializePresenter() {
        /* no-op */
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override protected void onPause() {
    super.onPause();
  }
}
