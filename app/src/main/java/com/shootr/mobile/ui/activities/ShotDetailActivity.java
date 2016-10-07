package com.shootr.mobile.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.shootr.mobile.R;
import com.shootr.mobile.domain.dagger.TemporaryFilesDir;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.ToolbarDecorator;
import com.shootr.mobile.ui.adapters.ShotDetailWithRepliesAdapter;
import com.shootr.mobile.ui.adapters.listeners.AvatarClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnNiceShotListener;
import com.shootr.mobile.ui.adapters.listeners.OnParentShownListener;
import com.shootr.mobile.ui.adapters.listeners.OnUrlClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnUsernameClickListener;
import com.shootr.mobile.ui.adapters.listeners.OnVideoClickListener;
import com.shootr.mobile.ui.adapters.listeners.ShotClickListener;
import com.shootr.mobile.ui.component.PhotoPickerController;
import com.shootr.mobile.ui.fragments.NewShotBarViewDelegate;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.presenter.NewShotBarPresenter;
import com.shootr.mobile.ui.presenter.PinShotPresenter;
import com.shootr.mobile.ui.presenter.ShotDetailPresenter;
import com.shootr.mobile.ui.views.NewShotBarView;
import com.shootr.mobile.ui.views.PinShotView;
import com.shootr.mobile.ui.views.ShotDetailView;
import com.shootr.mobile.ui.widgets.EndOffsetItemDecoration;
import com.shootr.mobile.util.AnalyticsTool;
import com.shootr.mobile.util.AndroidTimeUtils;
import com.shootr.mobile.util.BackStackHandler;
import com.shootr.mobile.util.Clipboard;
import com.shootr.mobile.util.CrashReportTool;
import com.shootr.mobile.util.CustomContextMenu;
import com.shootr.mobile.util.FeedbackMessage;
import com.shootr.mobile.util.ImageLoader;
import com.shootr.mobile.util.Intents;
import com.shootr.mobile.util.MenuItemValueHolder;
import com.shootr.mobile.util.NumberFormatUtil;
import com.shootr.mobile.util.ShareManager;
import com.shootr.mobile.util.TimeFormatter;
import com.shootr.mobile.util.WritePermissionManager;
import java.io.File;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class ShotDetailActivity extends BaseToolbarDecoratedActivity
  implements ShotDetailView, NewShotBarView, PinShotView {

    public static final String EXTRA_SHOT = "shot";
    public static final String EXTRA_ID_SHOT = "idShot";
    public static final String EXTRA_IS_IN_TIMELINE = "isIntimeline";
    private static final int OFFSET = 500;
    private static final int OFFSET_WITH_REPLIES = 400;

    @BindView(R.id.shot_detail_list) RecyclerView detailList;
    @BindView(R.id.shot_bar_text) TextView replyPlaceholder;
    @BindView(R.id.shot_bar_drafts) View replyDraftsButton;
    @BindString(R.string.shot_shared_message) String shotShared;
    @BindString(R.string.analytics_screen_shot_detail) String analyticsScreenShotDetail;
    @BindString(R.string.analytics_action_photo) String analyticsActionPhoto;
    @BindString(R.string.analytics_label_photo) String analyticsLabelPhoto;
    @BindString(R.string.analytics_action_nice) String analyticsActionNice;
    @BindString(R.string.analytics_label_nice) String analyticsLabelNice;
    @BindString(R.string.analytics_action_share_shot) String analyticsActionShareShot;
    @BindString(R.string.analytics_label_share_shot) String analyticsLabelShareShot;
    @BindString(R.string.analytics_action_external_share) String analyticsActionExternalShare;
    @BindString(R.string.analytics_label_external_share) String analyticsLabelExternalShare;
    @BindString(R.string.analytics_label_open_link) String analyticsLabelOpenlink;
    @BindString(R.string.analytics_action_open_link) String analyticsActionOpenLink;
    @BindString(R.string.analytics_source_shot_detail) String shotDetailSource;

    @Inject ImageLoader imageLoader;
    @Inject TimeFormatter timeFormatter;
    @Inject NumberFormatUtil numberFormatUtil;
    @Inject AndroidTimeUtils timeUtils;
    @Inject ShotDetailPresenter detailPresenter;
    @Inject NewShotBarPresenter newShotBarPresenter;
    @Inject ShareManager shareManager;
    @Inject FeedbackMessage feedbackMessage;
    @Inject @TemporaryFilesDir File tmpFiles;
    @Inject WritePermissionManager writePermissionManager;
    @Inject BackStackHandler backStackHandler;
    @Inject CrashReportTool crashReportTool;
    @Inject PinShotPresenter pinShotPresenter;
    @Inject AnalyticsTool analyticsTool;
    @Inject SessionRepository sessionRepository;

    private PhotoPickerController photoPickerController;
    private NewShotBarViewDelegate newShotBarViewDelegate;
    private ShotDetailWithRepliesAdapter detailAdapter;

    private MenuItemValueHolder copyShotMenuItem = new MenuItemValueHolder();

    private LinearLayoutManager linearLayoutManager;
    private int overallYScroll;
    private int replies = 0;
    private String idUser;

    public static Intent getIntentForActivity(Context context, ShotModel shotModel) {
        Intent intent = new Intent(context, ShotDetailActivity.class);
        intent.putExtra(EXTRA_SHOT, shotModel);
        return intent;
    }

    public static Intent getIntentForActivityFromTimeline(Context context, ShotModel shotModel) {
        Intent intent = new Intent(context, ShotDetailActivity.class);
        intent.putExtra(EXTRA_SHOT, shotModel);
        intent.putExtra(EXTRA_IS_IN_TIMELINE, true);
        return intent;
    }

    public static Intent getIntentForActivity(Context context, String idShot) {
        Intent intent = new Intent(context, ShotDetailActivity.class);
        intent.putExtra(EXTRA_ID_SHOT, idShot);
        return intent;
    }

    @Override protected int getLayoutResource() {
        return R.layout.activity_shot_detail;
    }

    @Override protected void setupToolbar(ToolbarDecorator toolbarDecorator) {
        toolbarDecorator.hideTitle();
    }

    @Override protected void initializeViews(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        analyticsTool.analyticsStart(getBaseContext(), analyticsScreenShotDetail);
        writePermissionManager.init(this);
        setupPhotoPicker();
        ShotModel shotModel = extractShotFromIntent();
        if (shotModel != null) {
            setupNewShotBarDelegate(shotModel);
            idUser = shotModel.getIdUser();
        }
        setupAdapter();
    }

    @Override protected void initializePresenter() {
        ShotModel shotModel = extractShotFromIntent();
        if (shotModel != null) {
            initializePresenter(shotModel);
        } else {
            String idShot = getIntent().getStringExtra(EXTRA_ID_SHOT);
            initializePresenter(idShot);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoPickerController.onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onResume() {
        super.onResume();
        detailPresenter.resume();
        pinShotPresenter.resume();
        newShotBarPresenter.resume();
    }

    @Override protected void onPause() {
        super.onPause();
        detailPresenter.pause();
        pinShotPresenter.pause();
        newShotBarPresenter.pause();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shot_detail, menu);
        copyShotMenuItem.bindRealMenuItem(menu.findItem(R.id.menu_copy_text));
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backStackHandler.handleBackStack(this);
            return true;
        } else if (item.getItemId() == R.id.menu_share) {
            openContextualMenu();
        } else if (item.getItemId() == R.id.menu_copy_text) {
            Clipboard.copyShotComment(this, extractShotFromIntent());
        }
        return super.onOptionsItemSelected(item);
    }

    private void openContextualMenu() {
        new CustomContextMenu.Builder(this).addAction(R.string.menu_share_shot_via_shootr, new Runnable() {
            @Override public void run() {
                detailPresenter.shareShotViaShootr();
                sendShareShotAnalythics();
            }
        }).addAction(R.string.menu_share_shot_via, new Runnable() {
            @Override public void run() {
                detailPresenter.shareShot();
                sendExternalShareAnalythics();
            }
        }).show();
    }

    private void sendExternalShareAnalythics() {
        AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
        builder.setContext(getBaseContext());
        builder.setActionId(analyticsActionExternalShare);
        builder.setLabelId(analyticsLabelExternalShare);
        builder.setSource(shotDetailSource);
        builder.setUser(sessionRepository.getCurrentUser());
        builder.setIdTargetUser(idUser);
        analyticsTool.analyticsSendAction(builder);
    }

    private void sendShareShotAnalythics() {
        AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
        builder.setContext(getBaseContext());
        builder.setActionId(analyticsActionShareShot);
        builder.setLabelId(analyticsLabelShareShot);
        builder.setSource(shotDetailSource);
        builder.setUser(sessionRepository.getCurrentUser());
        builder.setIdTargetUser(idUser);
        if (detailAdapter.getMainShot() != null) {
            builder.setTargetUsername(detailAdapter.getMainShot().getUsername());
            builder.setIdStream(detailAdapter.getMainShot().getStreamId());
            builder.setStreamName(detailAdapter.getMainShot().getStreamTitle());
        }
        analyticsTool.analyticsSendAction(builder);
    }

    @Override public void shareShot(ShotModel shotModel) {
        Intent shareIntent = shareManager.shareShotIntent(this, shotModel);
        Intents.maybeStartActivity(this, shareIntent);
    }

    @Override public void goToNicers(String idShot) {
        startActivity(NicersActivity.newIntent(this, idShot));
    }

    private void setupAdapter() {
        detailAdapter = new ShotDetailWithRepliesAdapter(imageLoader, new AvatarClickListener() {
            @Override public void onClick(String userId) {
                onShotAvatarClick(userId);
            }
        }, //
            new ShotClickListener() {
                @Override public void onClick(ShotModel shot) {
                    onShotClick(shot);
                }
            }, new ShotClickListener() {
            @Override public void onClick(ShotModel shot) {
                onShotClick(shot);
            }
        }, new ShotClickListener() {
            @Override public void onClick(ShotModel shotModel) {
                onStreamTitleClick(shotModel);
            }
        }, new ShotClickListener() {
            @Override public void onClick(ShotModel shot) {
                onShotImageClick(shot);
            }
        }, //
            new OnVideoClickListener() {
                @Override public void onVideoClick(String url) {
                    onShotVideoClick(url);
                }
            }, //
            new OnUsernameClickListener() {
                @Override public void onUsernameClick(String username) {
                    onShotUsernameClick(username);
                }
            }, //
            numberFormatUtil, new ShotClickListener() {

                @Override public void onClick(ShotModel shot) {
                    pinShotPresenter.pinToProfile(shot);
                }
            }, new OnParentShownListener() {
            @Override public void onShown(Integer parentsNumber, Integer repliesNumber) {
                replies = repliesNumber;
                linearLayoutManager.scrollToPositionWithOffset(parentsNumber, 0);
                if (repliesNumber == 0) {
                    detailList.addItemDecoration(new EndOffsetItemDecoration(OFFSET_WITH_REPLIES));
                }
            }
        }, //
            new OnNiceShotListener() {
                @Override public void markNice(ShotModel shot) {
                    detailPresenter.markNiceShot(shot.getIdShot());
                    sendAnalythics(shot);
                }
                @Override public void unmarkNice(String idShot) {
                    detailPresenter.unmarkNiceShot(idShot);
                }
            }, //
            new ShotClickListener() {
                @Override public void onClick(ShotModel shotModel) {
                    detailPresenter.openShotNicers(shotModel);
                }
            }, new OnUrlClickListener() {
            @Override public void onClick() {
                detailPresenter.storeClickCount();
                sendOpenlinkAnalythics();
            }
        }, timeFormatter, getResources(), timeUtils);
        setupDetailList();
    }

    private void sendOpenlinkAnalythics() {
        AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
        builder.setContext(getBaseContext());
        builder.setActionId(analyticsActionOpenLink);
        builder.setLabelId(analyticsLabelOpenlink);
        builder.setSource(shotDetailSource);
        builder.setUser(sessionRepository.getCurrentUser());
        analyticsTool.analyticsSendAction(builder);
    }

    private void sendAnalythics(ShotModel shot) {
        AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
        builder.setContext(getBaseContext());
        builder.setActionId(analyticsActionNice);
        builder.setLabelId(analyticsLabelNice);
        builder.setSource(shotDetailSource);
        builder.setUser(sessionRepository.getCurrentUser());
        builder.setIdTargetUser(shot.getIdUser());
        builder.setTargetUsername(shot.getUsername());
        analyticsTool.analyticsSendAction(builder);
    }

    private void setupDetailList() {
        linearLayoutManager = new LinearLayoutManager(this);
        detailList.setLayoutManager(linearLayoutManager);
        detailList.setAdapter(detailAdapter);
        detailList.addItemDecoration(new EndOffsetItemDecoration(OFFSET));
        detailList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                overallYScroll = overallYScroll + dy;
                if (overallYScroll > 0 && replies == 0) {
                    linearLayoutManager.scrollToPositionWithOffset(detailAdapter.getItemCount() - 1, 0);
                    overallYScroll = 0;
                }
            }
        });
    }

    private void onStreamTitleClick(ShotModel shotModel) {
        detailPresenter.streamTitleClick(shotModel);
    }

    private void setupPhotoPicker() {
        if (tmpFiles != null) {
            setupPhotoControllerWithTmpFilesDir();
        } else {
            crashReportTool.logException("Picker must have a temporary files directory.");
        }
    }

    private void setupPhotoControllerWithTmpFilesDir() {
        photoPickerController = new PhotoPickerController.Builder().onActivity(this)
          .withTemporaryDir(tmpFiles)
          .withHandler(new PhotoPickerController.Handler() {
              @Override public void onSelected(File imageFile) {
                  newShotBarPresenter.newShotImagePicked(imageFile);
              }

              @Override public void onError(Exception e) {
                  Timber.e(e, "Error selecting image");
              }

              @Override public void startPickerActivityForResult(Intent intent, int requestCode) {
                  startActivityForResult(intent, requestCode);
              }

              @Override public void openEditTopicDialog() {
                  /* no-op */
              }
          })
          .build();
    }

    public void setupNewShotBarDelegate(final ShotModel shotModel) {
        newShotBarViewDelegate = new NewShotBarViewDelegate(photoPickerController, replyDraftsButton, feedbackMessage) {
            @Override public void openNewShotView() {
                Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                  .from(ShotDetailActivity.this) //
                  .inReplyTo(shotModel.getIdShot(), shotModel.getUsername()) //
                  .build();
                startActivity(newShotIntent);
            }

            @Override public void openNewShotViewWithImage(File image) {
                Intent newShotIntent = PostNewShotActivity.IntentBuilder //
                  .from(ShotDetailActivity.this) //
                  .withImage(image) //
                  .inReplyTo(shotModel.getIdShot(), shotModel.getUsername()) //
                  .build();
                startActivity(newShotIntent);
            }

            @Override public void openEditTopicDialog() {
                /* no-op */
            }
        };
    }

    @Override public void initializeNewShotBarPresenter(String streamId) {
        newShotBarPresenter.initialize(this, streamId, false);
    }

    @Override public void openShot(ShotModel shotModel) {
        startActivity(ShotDetailActivity.getIntentForActivity(this, shotModel));
    }

    @Override public void goToStreamTimeline(String idStream) {
        startActivity(StreamTimelineActivity.newIntent(this, idStream));
    }

    @Override public void disableStreamTitle() {
        detailAdapter.disableStreamTitle();
    }

    @Override public void enableStreamTitle() {
        detailAdapter.enableStreamTitle();
    }

    private ShotModel extractShotFromIntent() {
        return (ShotModel) getIntent().getSerializableExtra(EXTRA_SHOT);
    }

    private void initializePresenter(ShotModel shotModel) {
        detailPresenter.initialize(this, shotModel);
        newShotBarPresenter.initialize(this, shotModel.getStreamId(), false);
    }

    private void initializePresenter(String idShot) {
        detailPresenter.initialize(this, idShot);
    }

    //region Listeners
    public void onShotImageClick(ShotModel shot) {
        detailPresenter.imageClick(shot);
        AnalyticsTool.Builder builder = new AnalyticsTool.Builder();
        builder.setContext(getBaseContext());
        builder.setActionId(analyticsActionPhoto);
        builder.setLabelId(analyticsLabelPhoto);
        builder.setSource(shotDetailSource);
        builder.setUser(sessionRepository.getCurrentUser());
        analyticsTool.analyticsSendAction(builder);
    }

    public void onShotAvatarClick(String userId) {
        detailPresenter.avatarClick(userId);
    }

    public void onShotClick(ShotModel shotModel) {
        detailPresenter.shotClick(shotModel);
    }

    public void onShotUsernameClick(String username) {
        detailPresenter.usernameClick(username);
    }

    private void onShotVideoClick(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @OnClick(R.id.shot_bar_text) public void onReplyClick() {
        newShotBarPresenter.newShotFromTextBox();
    }

    @OnClick(R.id.shot_bar_photo) public void onStartNewShotWithPhoto() {
        if (writePermissionManager.hasWritePermission()) {
            newShotBarPresenter.newShotFromImage();
        } else {
            writePermissionManager.requestWritePermissionToUser();
        }
    }

    @OnClick(R.id.shot_bar_drafts) public void openDrafts() {
        startActivity(new Intent(this, DraftsActivity.class));
    }
    //endregion

    //region View methods
    @Override public void renderShot(ShotModel shotModel) {
        detailAdapter.renderMainShot(shotModel);
        pinShotPresenter.initialize(this, shotModel);
        setupStreamTitle();
    }

    private void setupStreamTitle() {
        Boolean isInStreamTimeline = getIntent().getBooleanExtra(EXTRA_IS_IN_TIMELINE, false);
        detailPresenter.setupStreamTitle(isInStreamTimeline);
    }

    @Override public void renderReplies(List<ShotModel> shotModels) {
        detailAdapter.renderReplies(shotModels);
    }

    @Override public void openImage(String imageUrl) {
        Intent intentForImage = PhotoViewActivity.getIntentForActivity(this, imageUrl);
        startActivity(intentForImage);
    }

    @Override public void openProfile(String idUser) {
        Intent intentForUser = ProfileActivity.getIntent(this, idUser);
        startActivity(intentForUser);
        overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
    }

    @Override public void setReplyUsername(String username) {
        replyPlaceholder.setText(getString(R.string.reply_placeholder_pattern, username));
    }

    @Override public void scrollToBottom() {
        detailList.smoothScrollToPosition(detailAdapter.getItemCount() - 1);
    }

    @Override public void renderParents(List<ShotModel> parentShot) {
        detailAdapter.renderParentShot(parentShot);
    }

    @Override public void startProfileContainerActivity(String username) {
        Intent intentForUser = ProfileActivity.getIntentWithUsername(this, username);
        startActivity(intentForUser);
    }

    @Override public void showError(String errorMessage) {
        feedbackMessage.show(getView(), errorMessage);
    }

    @Override public void showShotShared() {
        feedbackMessage.show(getView(), shotShared);
    }

    @Override public void openNewShotView() {
        newShotBarViewDelegate.openNewShotView();
    }

    @Override public void pickImage() {
        newShotBarViewDelegate.pickImage();
    }

    @Override public void showHolderOptions() {
        /* no-op */
    }

    @Override public void openNewShotViewWithImage(File image) {
        newShotBarViewDelegate.openNewShotViewWithImage(image);
    }

    @Override public void openEditTopicDialog() {
        /* no-op */
    }

    @Override public void showDraftsButton() {
        newShotBarViewDelegate.showDraftsButton();
    }

    @Override public void hideDraftsButton() {
        newShotBarViewDelegate.hideDraftsButton();
    }

    @Override public void notifyPinnedShot(ShotModel shotModel) {
        detailPresenter.initialize(this, shotModel);
    }

    @Override public void showPinned() {
        feedbackMessage.show(getView(), R.string.shot_pinned);
    }

    @Override public void hidePinShotButton() {
        detailAdapter.hidePinToProfileButton();
    }

    @Override public void showPinShotButton() {
        detailAdapter.showPinToProfileContainer();
    }

    //endregion
}
