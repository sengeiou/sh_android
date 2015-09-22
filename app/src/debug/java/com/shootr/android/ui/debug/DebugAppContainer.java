package com.shootr.android.ui.debug;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.pedrovgs.lynx.LynxActivity;
import com.shootr.android.BuildConfig;
import com.shootr.android.R;
import com.shootr.android.ShootrApplication;
import com.shootr.android.data.AnimationSpeed;
import com.shootr.android.data.ApiEndpoint;
import com.shootr.android.data.ApiEndpoints;
import com.shootr.android.data.CustomEndpoint;
import com.shootr.android.data.DebugMode;
import com.shootr.android.data.NetworkEnabled;
import com.shootr.android.data.PicassoDebugging;
import com.shootr.android.data.PollerEnabled;
import com.shootr.android.data.ScalpelEnabled;
import com.shootr.android.data.ScalpelWireframeEnabled;
import com.shootr.android.data.ServerDownErrorInterceptor;
import com.shootr.android.data.UnauthorizedErrorInterceptor;
import com.shootr.android.data.VersionOutdatedErrorInterceptor;
import com.shootr.android.data.prefs.BooleanPreference;
import com.shootr.android.data.prefs.IntPreference;
import com.shootr.android.data.prefs.NotificationsEnabled;
import com.shootr.android.data.prefs.StringPreference;
import com.shootr.android.db.ShootrDbOpenHelper;
import com.shootr.android.service.DebugServiceAdapter;
import com.shootr.android.ui.AppContainer;
import com.shootr.android.ui.activities.MainTabbedActivity;
import com.shootr.android.ui.debug.debugactions.FakeEmailInUseDebugAction;
import com.shootr.android.ui.debug.debugactions.FakeUsernameInUseDebugAction;
import com.shootr.android.ui.debug.debugactions.LoginDebugAction;
import com.sloydev.jsonadapters.JsonAdapter;
import com.sloydev.okresponsefaker.EmptyBodyFakeResponse;
import com.sloydev.okresponsefaker.ResponseFaker;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.MockRestAdapter;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static butterknife.ButterKnife.findById;

/**
 * An {@link AppContainer} for debug builds which wrap the content view with a sliding drawer on
 * the right that holds all of the debug information and settings.
 */
@Singleton
public class DebugAppContainer implements AppContainer {

    private static final DateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("HH:mm dd-MM-yyyy");

    private final JsonAdapter jsonAdapter;
    private final OkHttpClient client;
    private final Picasso picasso;
    private final StringPreference networkEndpoint;
    private final BooleanPreference networkEnabled;
    private final BooleanPreference debugMode;
    private final StringPreference networkProxy;
    private final IntPreference animationSpeed;
    private final BooleanPreference picassoDebugging;
    private final BooleanPreference scalpelEnabled;
    private final BooleanPreference scalpelWireframeEnabled;
    private final BooleanPreference captureIntentsEnabled;
    private final BooleanPreference notificationsEnabled;
    private final BooleanPreference pollerEnabled;
    private StringPreference customEndpoint;
    //      private final RestAdapter restAdapter;
    private final DebugServiceAdapter debugServiceAdapter;
    private final MockRestAdapter mockRestAdapter;
    private final Application app;

    Activity activity;
    Context drawerContext;
    ContextualDebugActions contextualDebugActions;

    @Inject
    public DebugAppContainer(JsonAdapter jsonAdapter,
      OkHttpClient client,
      Picasso picasso,
      @ApiEndpoint StringPreference networkEndpoint,
      @NetworkEnabled BooleanPreference networkEnabled,
      @DebugMode BooleanPreference debugMode,
      @NetworkProxy StringPreference networkProxy,
      @AnimationSpeed IntPreference animationSpeed,
      @PicassoDebugging BooleanPreference picassoDebugging,
      @ScalpelEnabled BooleanPreference scalpelEnabled,
      @ScalpelWireframeEnabled BooleanPreference scalpelWireframeEnabled,
      @CaptureIntents BooleanPreference captureIntentsEnabled,
      @CustomEndpoint StringPreference customEndpoint,
      @NotificationsEnabled BooleanPreference notificationsEnabled,
      @PollerEnabled BooleanPreference pollerEnabled,
      DebugServiceAdapter debugServiceAdapter,
      MockRestAdapter mockRestAdapter,
      Application app) {
        this.jsonAdapter = jsonAdapter;
        this.client = client;
        this.picasso = picasso;
        this.debugMode = debugMode;
        this.networkProxy = networkProxy;
        this.networkEnabled = networkEnabled;
        this.networkEndpoint = networkEndpoint;
        this.scalpelEnabled = scalpelEnabled;
        this.scalpelWireframeEnabled = scalpelWireframeEnabled;
        this.animationSpeed = animationSpeed;
        this.picassoDebugging = picassoDebugging;
        this.captureIntentsEnabled = captureIntentsEnabled;
        this.customEndpoint = customEndpoint;
        this.notificationsEnabled = notificationsEnabled;
        this.pollerEnabled = pollerEnabled;
        this.debugServiceAdapter = debugServiceAdapter;
        this.mockRestAdapter = mockRestAdapter;
        this.app = app;
    }

    @Bind(R.id.debug_drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.debug_content) ViewGroup content;

    @Bind(R.id.debug_contextual_title) View contextualTitleView;
    @Bind(R.id.debug_contextual_list) LinearLayout contextualListView;

    @Bind(R.id.debug_network_endpoint) Spinner endpointView;
    @Bind(R.id.debug_network_endpoint_edit) View endpointEditView;
    @Bind(R.id.debug_network_debugmode) Switch debugModeView;
    @Bind(R.id.debug_network_enabled) Switch networkEnabledView;
    @Bind(R.id.debug_network_delay) Spinner networkDelayView;
    @Bind(R.id.debug_network_variance) Spinner networkVarianceView;
    @Bind(R.id.debug_network_error) Spinner networkErrorView;
    @Bind(R.id.debug_network_proxy) Spinner networkProxyView;
    @Bind(R.id.debug_network_poller) Switch networkPollerView;

    @Bind(R.id.debug_fake_only_once) Switch fakeRequestOnlyOnce;

    @Bind(R.id.debug_notif_enable) Switch notificationsEnabledView;

    @Bind(R.id.debug_ui_animation_speed) Spinner uiAnimationSpeedView;
    @Bind(R.id.debug_ui_pixel_grid) Switch uiPixelGridView;
    @Bind(R.id.debug_ui_pixel_ratio) Switch uiPixelRatioView;
    @Bind(R.id.debug_ui_scalpel) Switch uiScalpelView;
    @Bind(R.id.debug_ui_scalpel_wireframe) Switch uiScalpelWireframeView;
    @Bind(R.id.debug_capture_intents) Switch uiCaptureIntents;

    @Bind(R.id.debug_build_name) TextView buildNameView;
    @Bind(R.id.debug_build_code) TextView buildCodeView;
    @Bind(R.id.debug_build_sha) TextView buildShaView;
    @Bind(R.id.debug_build_branch) TextView buildBranchView;

    @Bind(R.id.debug_device_make) TextView deviceMakeView;
    @Bind(R.id.debug_device_model) TextView deviceModelView;
    @Bind(R.id.debug_device_resolution) TextView deviceResolutionView;
    @Bind(R.id.debug_device_density) TextView deviceDensityView;
    @Bind(R.id.debug_device_release) TextView deviceReleaseView;
    @Bind(R.id.debug_device_api) TextView deviceApiView;
    @Bind(R.id.debug_logs_show) TextView deviceLogView;
    @Bind(R.id.debug_device_database_extract) Button deviceDatabaseExtractView;

    @Bind(R.id.debug_picasso_indicators) Switch picassoIndicatorView;
    @Bind(R.id.debug_picasso_cache_size) TextView picassoCacheSizeView;
    @Bind(R.id.debug_picasso_cache_hit) TextView picassoCacheHitView;
    @Bind(R.id.debug_picasso_cache_miss) TextView picassoCacheMissView;
    @Bind(R.id.debug_picasso_decoded) TextView picassoDecodedView;
    @Bind(R.id.debug_picasso_decoded_total) TextView picassoDecodedTotalView;
    @Bind(R.id.debug_picasso_decoded_avg) TextView picassoDecodedAvgView;
    @Bind(R.id.debug_picasso_transformed) TextView picassoTransformedView;
    @Bind(R.id.debug_picasso_transformed_total) TextView picassoTransformedTotalView;
    @Bind(R.id.debug_picasso_transformed_avg) TextView picassoTransformedAvgView;

    @Override
    public ViewGroup get(final Activity activity) {
        this.activity = activity;
        drawerContext = activity;

        activity.setContentView(R.layout.debug_activity_frame);

        // Manually find the debug drawer and inflate the drawer layout inside of it.
        ViewGroup drawer = findById(activity, R.id.debug_drawer);
        LayoutInflater.from(drawerContext).inflate(R.layout.debug_drawer_content, drawer);

        // Inject after inflating the drawer layout so its views are available to inject.
        ButterKnife.bind(this, activity);

        drawerLayout.setDrawerShadow(R.drawable.debug_drawer_shadow, Gravity.END);
        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                refreshPicassoStats();
            }
        });

        setupContextualActions(activity);
        setupNetworkSection();
        setupFakeRequestsSection();
        setupNotificationsSection();
        setupUserInterfaceSection();
        setupBuildSection();
        setupDeviceSection();
        setupPicassoSection();

        return content;
    }

    private void setupContextualActions(Activity activity) {
        contextualDebugActions = new ContextualDebugActions(this, debugActions());
        contextualDebugActions.setActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });
        contextualDebugActions.bindActions(activity);
    }

    private Collection<ContextualDebugActions.DebugAction<? extends Activity>> debugActions() {
        List<ContextualDebugActions.DebugAction<?>> debugActions = new LinkedList<>();
        debugActions.add(new FakeEmailInUseDebugAction(jsonAdapter));
        debugActions.add(new FakeUsernameInUseDebugAction(jsonAdapter));
        debugActions.add(new LoginDebugAction("rafa", "123456"));
        debugActions.add(new LoginDebugAction("artjimlop", "papafrita"));
        debugActions.add(new LoginDebugAction("heisenberg", "123456"));
        debugActions.add(new LoginDebugAction("ensaladilla", "123456"));
        debugActions.add(new LoginDebugAction("fakeuser", "fakepassword"));
        return debugActions;
    }


    private void setupNetworkSection() {
        //region Endpoint
        final ApiEndpoints currentEndpoint = ApiEndpoints.from(networkEndpoint.get());
        final EnumAdapter<ApiEndpoints> endpointAdapter = new EnumAdapter<>(drawerContext, ApiEndpoints.class);
        endpointView.setAdapter(endpointAdapter);
        endpointView.setSelection(currentEndpoint.ordinal());
        endpointView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                ApiEndpoints selected = endpointAdapter.getItem(position);
                if (selected != currentEndpoint) {
                    if (selected == ApiEndpoints.CUSTOM) {
                        Timber.d("Custom network endpoint selected. Prompting for URL.");

                        showCustomEndpointDialog(currentEndpoint.ordinal(), customEndpoint.get());
                    } else {
                        setEndpointAndRelaunch(selected.url);
                    }
                } else {
                    Timber.d("Ignoring re-selection of network endpoint %s", selected);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //endregion

        //region Debug mode
        boolean debugModeValue = debugMode.get();
        debugModeView.setChecked(debugModeValue);
        debugModeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("Setting Debug Mode to " + isChecked);
                debugMode.set(isChecked);
                relaunch();
            }
        });
        //endregion

        //region Network enabled
        boolean networkEnabledValue = networkEnabled.get();
        networkEnabledView.setChecked(networkEnabledValue);
        networkEnabledView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("Setting Network Enabled to " + isChecked);
                networkEnabled.set(isChecked);
                debugServiceAdapter.setConnected(isChecked);
            }
        });
        //endregion

        //region Delay
        final NetworkDelayAdapter delayAdapter = new NetworkDelayAdapter(drawerContext);
        networkDelayView.setAdapter(delayAdapter);
        networkDelayView.setSelection(NetworkDelayAdapter.getPositionForValue(debugServiceAdapter.getDelay()));
        networkDelayView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                long selected = delayAdapter.getItem(position);
                if (selected != debugServiceAdapter.getDelay()) {
                    Timber.d("Setting network delay to %sms", selected);
                    debugServiceAdapter.setDelay((int) selected);
                    mockRestAdapter.setDelay(selected);
                } else {
                    Timber.d("Ignoring re-selection of network delay %sms", selected);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //endregion

        //region Variance
        final NetworkVarianceAdapter varianceAdapter = new NetworkVarianceAdapter(drawerContext);
        networkVarianceView.setAdapter(varianceAdapter);
        networkVarianceView.setSelection(
          NetworkVarianceAdapter.getPositionForValue(debugServiceAdapter.getVariancePercentage()));
        networkVarianceView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int selected = varianceAdapter.getItem(position);
                if (selected != debugServiceAdapter.getVariancePercentage()) {
                    Timber.d("Setting network variance to %s%%", selected);
                    debugServiceAdapter.setVariancePercentage(selected);
                    mockRestAdapter.setVariancePercentage(selected);
                } else {
                    Timber.d("Ignoring re-selection of network variance %s%%", selected);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //endregion

        //region Error
        final NetworkErrorAdapter errorAdapter = new NetworkErrorAdapter(drawerContext);
        networkErrorView.setAdapter(errorAdapter);
        networkErrorView.setSelection(NetworkErrorAdapter.getPositionForValue(debugServiceAdapter.getErrorPercentage()));
        networkErrorView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int selected = errorAdapter.getItem(position);
                if (selected != debugServiceAdapter.getErrorPercentage()) {
                    Timber.d("Setting network error to %s%%", selected);
                    debugServiceAdapter.setErrorPercentage(selected);
                    mockRestAdapter.setErrorPercentage(selected);
                } else {
                    Timber.d("Ignoring re-selection of network error %s%%", selected);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //endregion

        //region Proxy
        int currentProxyPosition = client.getProxy() != null ? ProxyAdapter.PROXY : ProxyAdapter.NONE;
        final ProxyAdapter proxyAdapter = new ProxyAdapter(activity, networkProxy);
        networkProxyView.setAdapter(proxyAdapter);
        networkProxyView.setSelection(currentProxyPosition);
        networkProxyView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == ProxyAdapter.NONE) {
                    Timber.d("Disabling network proxy");
                    //networkProxy.delete();
                    client.setProxy(null);
                } else if (networkProxy.isSet() && position == ProxyAdapter.PROXY) {
                    setProxy(networkProxy.get());
                    Timber.d("Setting previous proxy %s", networkProxy.get());
                } else {
                    Timber.d("New network proxy selected. Prompting for host.");
                    showNewNetworkProxyDialog(proxyAdapter);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //endregion

        // Only show the endpoint editor when a custom endpoint is in use.
        endpointEditView.setVisibility(currentEndpoint == ApiEndpoints.CUSTOM ? VISIBLE : GONE);

        if (debugMode.get()) {
            // Disable network proxy if we are in mock mode.
            networkProxyView.setEnabled(false);
        } else {
            // Disable network controls if we are not in mock mode.
            networkEnabledView.setEnabled(false);
            networkDelayView.setEnabled(false);
            networkVarianceView.setEnabled(false);
            networkErrorView.setEnabled(false);
        }

        boolean pollerEnabledValue = pollerEnabled.get();
        networkPollerView.setChecked(pollerEnabledValue);
        networkPollerView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("Setting Poller Enabled to " + isChecked);
                pollerEnabled.set(isChecked);
            }
        });
    }

    @OnClick(R.id.debug_network_endpoint_edit) void onEditEndpointClicked() {
        //    Timber.d("Prompting to edit custom endpoint URL.");
        // Pass in the currently selected position since we are merely editing.
        showCustomEndpointDialog(endpointView.getSelectedItemPosition(), networkEndpoint.get());
    }

    private void setupFakeRequestsSection() {
        fakeRequestOnlyOnce.setChecked(ResponseFaker.isTriggerOnce());
        fakeRequestOnlyOnce.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ResponseFaker.setTriggerOnce(isChecked);
            }
        });
    }

    @OnClick(R.id.debug_fake_version)
    public void onFakeVersionOutdatedRequest() {
        ResponseFaker.setNextFakeResponse(new EmptyBodyFakeResponse(VersionOutdatedErrorInterceptor.CODE_OUTDATED_VERSION));
    }

    @OnClick(R.id.debug_fake_server_down)
    public void onFakeServerDownRequest() {
        ResponseFaker.setNextFakeResponse(new EmptyBodyFakeResponse(ServerDownErrorInterceptor.CODE_SERVER_DOWN));
    }

    @OnClick(R.id.debug_fake_unauthorized)
    public void onFakeUnauthorizedRequest() {
        ResponseFaker.setNextFakeResponse(new EmptyBodyFakeResponse(UnauthorizedErrorInterceptor.CODE_UNAUTHORIZED));
    }

    private void setupNotificationsSection() {
        boolean showNotifications = notificationsEnabled.get();
        notificationsEnabledView.setChecked(showNotifications);
        notificationsEnabledView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("Setting notifications %s", isChecked ? "on" : "off");
                notificationsEnabled.set(isChecked);
            }
        });
    }

    private void setupUserInterfaceSection() {
        final AnimationSpeedAdapter speedAdapter = new AnimationSpeedAdapter(activity);
        uiAnimationSpeedView.setAdapter(speedAdapter);
        final int animationSpeedValue = animationSpeed.get();
        uiAnimationSpeedView.setSelection(AnimationSpeedAdapter.getPositionForValue(animationSpeedValue));
        uiAnimationSpeedView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int selected = speedAdapter.getItem(position);
                if (selected != animationSpeed.get()) {
                    Timber.d("Setting animation speed to %sx", selected);
                    animationSpeed.set(selected);
                    applyAnimationSpeed(selected);
                } else {
                    Timber.d("Ignoring re-selection of animation speed %sx", selected);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        // Ensure the animation speed value is always applied across app restarts.
        content.post(new Runnable() {
            @Override public void run() {
                applyAnimationSpeed(animationSpeedValue);
            }
        });

   /* boolean gridEnabled = pixelGridEnabled.get();
    madgeFrameLayout.setOverlayEnabled(gridEnabled);
    uiPixelGridView.setChecked(gridEnabled);
    uiPixelRatioView.setEnabled(gridEnabled);
    uiPixelGridView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Timber.d("Setting pixel grid overlay enabled to " + isChecked);
        pixelGridEnabled.set(isChecked);
        madgeFrameLayout.setOverlayEnabled(isChecked);
        uiPixelRatioView.setEnabled(isChecked);
      }
    });*/

  /*  boolean ratioEnabled = pixelRatioEnabled.get();
    madgeFrameLayout.setOverlayRatioEnabled(ratioEnabled);
    uiPixelRatioView.setChecked(ratioEnabled);
    uiPixelRatioView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Timber.d("Setting pixel scale overlay enabled to " + isChecked);
        pixelRatioEnabled.set(isChecked);
        madgeFrameLayout.setOverlayRatioEnabled(isChecked);
      }
    });
*/
       /* boolean scalpel = scalpelEnabled.get();
        scalpelFrameLayout.setLayerInteractionEnabled(scalpel);
        uiScalpelView.setChecked(scalpel);
        uiScalpelWireframeView.setEnabled(scalpel);
        uiScalpelView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        Timber.d("Setting scalpel interaction enabled to " + isChecked);
                scalpelEnabled.set(isChecked);
                scalpelFrameLayout.setLayerInteractionEnabled(isChecked);
                uiScalpelWireframeView.setEnabled(isChecked);
            }
        });

        boolean wireframe = scalpelWireframeEnabled.get();
        scalpelFrameLayout.setDrawViews(!wireframe);
        uiScalpelWireframeView.setChecked(wireframe);
        uiScalpelWireframeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        Timber.d("Setting scalpel wireframe enabled to " + isChecked);
                scalpelWireframeEnabled.set(isChecked);
                scalpelFrameLayout.setDrawViews(!isChecked);
            }
        });*/

        boolean captureIntents  = captureIntentsEnabled.get();
        uiCaptureIntents.setChecked(captureIntents);
        uiCaptureIntents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("Setting capture intents to " + isChecked);
                captureIntentsEnabled.set(isChecked);
            }
        });
    }

    private void setupBuildSection() {
        buildNameView.setText(BuildConfig.VERSION_NAME);
        buildCodeView.setText(String.valueOf(BuildConfig.VERSION_CODE));
        buildShaView.setText(BuildConfig.GIT_SHA);
        buildBranchView.setText(BuildConfig.GIT_BRANCH);
    }

    private void setupDeviceSection() {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        String densityBucket = getDensityString(displayMetrics);
        deviceMakeView.setText(Build.MANUFACTURER);
        deviceModelView.setText(Build.MODEL);
        deviceResolutionView.setText(displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);
        deviceDensityView.setText(displayMetrics.densityDpi + "dpi (" + densityBucket + ")");
        deviceReleaseView.setText(Build.VERSION.RELEASE);
        deviceApiView.setText(String.valueOf(Build.VERSION.SDK_INT));
    }

    @OnClick(R.id.debug_logs_show)
    public void openLog() {
        Intent lynxActivityIntent = LynxActivity.getIntent(drawerContext);
        drawerContext.startActivity(lynxActivityIntent);
    }

    @OnClick(R.id.debug_device_database_extract)
    public void extractDatabase() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath =
                  "//data//" + BuildConfig.APPLICATION_ID + "//databases//" + ShootrDbOpenHelper.DATABASE_NAME;
                String backupDBPath = ShootrDbOpenHelper.DATABASE_NAME;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                String copiedMessage = "Database copied to " + backupDB.getAbsolutePath();
                Toast.makeText(drawerContext, copiedMessage, Toast.LENGTH_LONG).show();
                Timber.i(copiedMessage);
            }
        } catch (Exception e) {
            Timber.e(e, "Error while copying database to sdcard");
        }
    }

    private void setupPicassoSection() {
        boolean picassoDebuggingValue = picassoDebugging.get();
        picasso.setIndicatorsEnabled(picassoDebuggingValue);
        picasso.setLoggingEnabled(picassoDebuggingValue);
        picassoIndicatorView.setChecked(picassoDebuggingValue);
        picassoIndicatorView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                Timber.d("Setting Picasso debugging to " + isChecked);
                picasso.setIndicatorsEnabled(isChecked);
                picasso.setLoggingEnabled(isChecked);
                picassoDebugging.set(isChecked);
            }
        });

        refreshPicassoStats();
    }

    private void refreshPicassoStats() {
        StatsSnapshot snapshot = picasso.getSnapshot();
        String size = getSizeString(snapshot.size);
        String total = getSizeString(snapshot.maxSize);
        int percentage = (int) ((1f * snapshot.size / snapshot.maxSize) * 100);
        picassoCacheSizeView.setText(size + " / " + total + " (" + percentage + "%)");
        picassoCacheHitView.setText(String.valueOf(snapshot.cacheHits));
        picassoCacheMissView.setText(String.valueOf(snapshot.cacheMisses));
        picassoDecodedView.setText(String.valueOf(snapshot.originalBitmapCount));
        picassoDecodedTotalView.setText(getSizeString(snapshot.totalOriginalBitmapSize));
        picassoDecodedAvgView.setText(getSizeString(snapshot.averageOriginalBitmapSize));
        picassoTransformedView.setText(String.valueOf(snapshot.transformedBitmapCount));
        picassoTransformedTotalView.setText(getSizeString(snapshot.totalTransformedBitmapSize));
        picassoTransformedAvgView.setText(getSizeString(snapshot.averageTransformedBitmapSize));
    }

    private void applyAnimationSpeed(int multiplier) {
        try {
            Method method = ValueAnimator.class.getDeclaredMethod("setDurationScale", float.class);
            method.invoke(null, (float) multiplier);
        } catch (Exception e) {
            Timber.e(e, "Unable to apply animation speed.");
        }
    }

    private static String getDensityString(DisplayMetrics displayMetrics) {
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            default:
                return "unknown";
        }
    }

    private static String getSizeString(long bytes) {
        String[] units = new String[] { "B", "KB", "MB", "GB" };
        int unit = 0;
        while (bytes >= 1024) {
            bytes /= 1024;
            unit += 1;
        }
        return bytes + units[unit];
    }

    private void showNewNetworkProxyDialog(final ProxyAdapter proxyAdapter) {
        final int originalSelection = networkProxy.isSet() ? ProxyAdapter.PROXY : ProxyAdapter.NONE;

        View view = LayoutInflater.from(activity).inflate(R.layout.debug_drawer_network_proxy, null);
        final EditText host = findById(view, R.id.debug_drawer_network_proxy_host);

        new AlertDialog.Builder(activity) //
          .setTitle("Set Network Proxy")
          .setView(view)
          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int i) {
                  networkProxyView.setSelection(originalSelection);
                  dialog.cancel();
              }
          })
          .setPositiveButton("Use", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int i) {
                  String theHost = host.getText().toString();
                  if (!TextUtils.isEmpty(theHost)) {
                      if (setProxy(theHost)) {
                          proxyAdapter.notifyDataSetChanged(); // Tell the spinner to update.
                          networkProxy.set(theHost); // Persist across restarts.
                          networkProxyView.setSelection(ProxyAdapter.PROXY); // And show the proxy.
                      } else {
                          Toast.makeText(activity, "Wrong proxy format", Toast.LENGTH_SHORT).show();
                      }
                  } else {
                      networkProxyView.setSelection(originalSelection);
                  }
              }
          })
          .setOnCancelListener(new DialogInterface.OnCancelListener() {
              @Override public void onCancel(DialogInterface dialogInterface) {
                  networkProxyView.setSelection(originalSelection);
              }
          })
          .show();
    }

    private void showCustomEndpointDialog(final int originalSelection, String defaultUrl) {
        View view = LayoutInflater.from(activity).inflate(R.layout.debug_drawer_network_endpoint, null);
        final EditText url = findById(view, R.id.debug_drawer_network_endpoint_url);
        url.setText(defaultUrl);
        url.setSelection(url.length());

        new AlertDialog.Builder(activity) //
          .setTitle("Set Network Endpoint")
          .setView(view)
          .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int i) {
                  endpointView.setSelection(originalSelection);
                  dialog.cancel();
              }
          })
          .setPositiveButton("Use", new DialogInterface.OnClickListener() {
              @Override public void onClick(DialogInterface dialog, int i) {
                  String theUrl = url.getText().toString();
                  if (!TextUtils.isEmpty(theUrl)) {
                      customEndpoint.set(theUrl);
                      setEndpointAndRelaunch(theUrl);
                  } else {
                      endpointView.setSelection(originalSelection);
                  }
              }
          })
          .setOnCancelListener(new DialogInterface.OnCancelListener() {
              @Override public void onCancel(DialogInterface dialogInterface) {
                  endpointView.setSelection(originalSelection);
              }
          })
          .show();
    }

    private void setEndpointAndRelaunch(String endpoint) {
        //    Timber.d("Setting network endpoint to %s", endpoint);
        networkEndpoint.set(endpoint);

        relaunch();
    }

    private boolean setProxy(String theHost) {
        try {
            String[] parts = theHost.split(":", 2);
            SocketAddress address = InetSocketAddress.createUnresolved(parts[0], Integer.parseInt(parts[1]));
            client.setProxy(new Proxy(Proxy.Type.HTTP, address));
            return true;
        } catch (Exception e) {
            Timber.w(e, "Wrong proxy format %s", theHost);
            return false;
        }
    }

    private void relaunch() {
        Intent newApp = new Intent(app, MainTabbedActivity.class);
        newApp.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        app.startActivity(newApp);
        ShootrApplication.get(app).buildObjectGraphAndInject();
    }

    public Context getContext() {
        return drawerContext;
    }
}
