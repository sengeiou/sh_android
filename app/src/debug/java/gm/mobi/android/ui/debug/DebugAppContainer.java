package gm.mobi.android.ui.debug;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.StatsSnapshot;
import gm.mobi.android.BuildConfig;
import gm.mobi.android.GolesApplication;
import gm.mobi.android.R;
import gm.mobi.android.data.AnimationSpeed;
import gm.mobi.android.data.ApiEndpoint;
import gm.mobi.android.data.ApiEndpoints;
import gm.mobi.android.data.CustomEndpoint;
import gm.mobi.android.data.NetworkEnabled;
import gm.mobi.android.data.PicassoDebugging;
import gm.mobi.android.data.ScalpelEnabled;
import gm.mobi.android.data.ScalpelWireframeEnabled;
import gm.mobi.android.data.SeenDebugDrawer;
import gm.mobi.android.data.prefs.BooleanPreference;
import gm.mobi.android.data.prefs.InitialSetupCompleted;
import gm.mobi.android.data.prefs.IntPreference;
import gm.mobi.android.data.prefs.StringPreference;
import gm.mobi.android.service.BagdadMockService;
import gm.mobi.android.ui.AppContainer;
import gm.mobi.android.ui.activities.LogReaderActivity;
import gm.mobi.android.ui.activities.MainActivity;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.inject.Singleton;
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
    private static final DateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm a");


    private final OkHttpClient client;
    private final Picasso picasso;
    private final StringPreference networkEndpoint;
    private final BooleanPreference networkEnabled;
    private final StringPreference networkProxy;
    private final IntPreference animationSpeed;
    private final BooleanPreference picassoDebugging;
    private final BooleanPreference scalpelEnabled;
    private final BooleanPreference scalpelWireframeEnabled;
    private final BooleanPreference seenDebugDrawer;
    private BooleanPreference initialSetupCompleted;
    private StringPreference customEndpoint;
    //      private final RestAdapter restAdapter;
    private final BagdadMockService mockBagdadService;
    private final Application app;

    Activity activity;
    Context drawerContext;
    @Inject
    public DebugAppContainer(OkHttpClient client, Picasso picasso,
                             @ApiEndpoint StringPreference networkEndpoint,
                             @NetworkEnabled BooleanPreference networkEnabled,
                             @NetworkProxy StringPreference networkProxy,
                             @AnimationSpeed IntPreference animationSpeed,
                             @PicassoDebugging BooleanPreference picassoDebugging,
                             @ScalpelEnabled BooleanPreference scalpelEnabled,
                             @ScalpelWireframeEnabled BooleanPreference scalpelWireframeEnabled,
                             @SeenDebugDrawer BooleanPreference seenDebugDrawer,
                             @InitialSetupCompleted BooleanPreference initialSetupCompleted,
                             @CustomEndpoint StringPreference customEndpoint,
                             BagdadMockService mockBagdadService,
                             Application app) {
        this.client = client;
        this.picasso = picasso;
        this.networkProxy = networkProxy;
        this.networkEnabled = networkEnabled;
        this.networkEndpoint = networkEndpoint;
        this.scalpelEnabled = scalpelEnabled;
        this.scalpelWireframeEnabled = scalpelWireframeEnabled;
        this.seenDebugDrawer = seenDebugDrawer;
        this.animationSpeed = animationSpeed;
        this.picassoDebugging = picassoDebugging;
        this.initialSetupCompleted = initialSetupCompleted;
        this.customEndpoint = customEndpoint;
        this.mockBagdadService = mockBagdadService;
        this.app = app;
    }

    @InjectView(R.id.debug_drawer_layout) DrawerLayout drawerLayout;
    @InjectView(R.id.debug_content) ViewGroup content;

//    @InjectView(R.id.debug_content) ScalpelFrameLayout scalpelFrameLayout;

    @InjectView(R.id.debug_contextual_title) View contextualTitleView;
    @InjectView(R.id.debug_contextual_list) LinearLayout contextualListView;

    @InjectView(R.id.debug_network_endpoint) Spinner endpointView;
    @InjectView(R.id.debug_network_endpoint_edit) View endpointEditView;
    @InjectView(R.id.debug_ui_pixel_grid) Switch networkEnabledView;
    @InjectView(R.id.debug_network_delay) Spinner networkDelayView;
    @InjectView(R.id.debug_network_variance) Spinner networkVarianceView;
    @InjectView(R.id.debug_network_error) Spinner networkErrorView;
    @InjectView(R.id.debug_network_proxy) Spinner networkProxyView;
    @InjectView(R.id.debug_network_logging) Spinner networkLoggingView;

    @InjectView(R.id.debug_ui_animation_speed) Spinner uiAnimationSpeedView;
    @InjectView(R.id.debug_ui_pixel_grid) Switch uiPixelGridView;
    @InjectView(R.id.debug_ui_pixel_ratio) Switch uiPixelRatioView;
    @InjectView(R.id.debug_ui_scalpel) Switch uiScalpelView;
    @InjectView(R.id.debug_ui_scalpel_wireframe) Switch uiScalpelWireframeView;

    @InjectView(R.id.debug_build_name) TextView buildNameView;
    @InjectView(R.id.debug_build_code) TextView buildCodeView;
    @InjectView(R.id.debug_build_sha) TextView buildShaView;
    @InjectView(R.id.debug_build_date) TextView buildDateView;

    @InjectView(R.id.debug_device_make) TextView deviceMakeView;
    @InjectView(R.id.debug_device_model) TextView deviceModelView;
    @InjectView(R.id.debug_device_resolution) TextView deviceResolutionView;
    @InjectView(R.id.debug_device_density) TextView deviceDensityView;
    @InjectView(R.id.debug_device_release) TextView deviceReleaseView;
    @InjectView(R.id.debug_device_api) TextView deviceApiView;
    @InjectView(R.id.debug_device_log) TextView deviceLogView;
    @InjectView(R.id.debug_device_database_delete) TextView deviceDatabaseDeleteView;

    @InjectView(R.id.debug_picasso_indicators) Switch picassoIndicatorView;
    @InjectView(R.id.debug_picasso_cache_size) TextView picassoCacheSizeView;
    @InjectView(R.id.debug_picasso_cache_hit) TextView picassoCacheHitView;
    @InjectView(R.id.debug_picasso_cache_miss) TextView picassoCacheMissView;
    @InjectView(R.id.debug_picasso_decoded) TextView picassoDecodedView;
    @InjectView(R.id.debug_picasso_decoded_total) TextView picassoDecodedTotalView;
    @InjectView(R.id.debug_picasso_decoded_avg) TextView picassoDecodedAvgView;
    @InjectView(R.id.debug_picasso_transformed) TextView picassoTransformedView;
    @InjectView(R.id.debug_picasso_transformed_total) TextView picassoTransformedTotalView;
    @InjectView(R.id.debug_picasso_transformed_avg) TextView picassoTransformedAvgView;

    @Override
    public ViewGroup get(final Activity activity) {
        this.activity = activity;
        drawerContext = activity;

        activity.setContentView(R.layout.debug_activity_frame);

        // Manually find the debug drawer and inflate the drawer layout inside of it.
        ViewGroup drawer = findById(activity, R.id.debug_drawer);
        LayoutInflater.from(drawerContext).inflate(R.layout.debug_drawer_content, drawer);

        // Inject after inflating the drawer layout so its views are available to inject.
        ButterKnife.inject(this, activity);

        // Set up the contextual actions to watch views coming in and out of the content area.
        Set<ContextualDebugActions.DebugAction<?>> debugActions = Collections.emptySet();
        ContextualDebugActions contextualActions = new ContextualDebugActions(this, debugActions);
        content.setOnHierarchyChangeListener(HierarchyTreeChangeListener.wrap(contextualActions));

        drawerLayout.setDrawerShadow(R.drawable.debug_drawer_shadow, Gravity.END);
        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                refreshPicassoStats();
            }
        });

        // If you have not seen the debug drawer before, show it with a message
        if (!seenDebugDrawer.get()) {
            drawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerLayout.openDrawer(Gravity.END);
                    Toast.makeText(activity, "Drawer welcome", Toast.LENGTH_LONG).show();
                }
            }, 1000);
            seenDebugDrawer.set(true);
        }

        setupNetworkSection();
        setupUserInterfaceSection();
        setupBuildSection();
        setupDeviceSection();
        setupPicassoSection();

        return content;
    }

    private void setupNetworkSection() {
    final ApiEndpoints currentEndpoint = ApiEndpoints.from(networkEndpoint.get());
    final EnumAdapter<ApiEndpoints> endpointAdapter =
        new EnumAdapter<>(drawerContext, ApiEndpoints.class);
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

        boolean networkEnabledValue = networkEnabled.get();
        networkEnabledView.setChecked(networkEnabledValue);
        networkEnabledView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timber.d("Setting Network Enabled to " + isChecked);
                networkEnabled.set(isChecked);
            }
        });

    final NetworkDelayAdapter delayAdapter = new NetworkDelayAdapter(drawerContext);
    networkDelayView.setAdapter(delayAdapter);
    networkDelayView.setSelection(
        NetworkDelayAdapter.getPositionForValue(mockBagdadService.getDelay()));
    networkDelayView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        long selected = delayAdapter.getItem(position);
        if (selected != mockBagdadService.getDelay()) {
          Timber.d("Setting network delay to %sms", selected);
          mockBagdadService.setDelay(selected);
        } else {
          Timber.d("Ignoring re-selection of network delay %sms", selected);
        }
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });

    final NetworkVarianceAdapter varianceAdapter = new NetworkVarianceAdapter(drawerContext);
    networkVarianceView.setAdapter(varianceAdapter);
    networkVarianceView.setSelection(
            NetworkVarianceAdapter.getPositionForValue(mockBagdadService.getVariancePercentage()));
    networkVarianceView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        int selected = varianceAdapter.getItem(position);
        if (selected != mockBagdadService.getVariancePercentage()) {
          Timber.d("Setting network variance to %s%%", selected);
          mockBagdadService.setVariancePercentage(selected);
        } else {
          Timber.d("Ignoring re-selection of network variance %s%%", selected);
        }
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });

    final NetworkErrorAdapter errorAdapter = new NetworkErrorAdapter(drawerContext);
    networkErrorView.setAdapter(errorAdapter);
    networkErrorView.setSelection(
            NetworkErrorAdapter.getPositionForValue(mockBagdadService.getErrorPercentage()));
    networkErrorView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        int selected = errorAdapter.getItem(position);
        if (selected != mockBagdadService.getErrorPercentage()) {
          Timber.d("Setting network error to %s%%", selected);
          mockBagdadService.setErrorPercentage(selected);
        } else {
          Timber.d("Ignoring re-selection of network error %s%%", selected);
        }
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });

    int currentProxyPosition = client.getProxy()!=null ? ProxyAdapter.PROXY : ProxyAdapter.NONE;
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

    // Only show the endpoint editor when a custom endpoint is in use.
    endpointEditView.setVisibility(currentEndpoint == ApiEndpoints.CUSTOM ? VISIBLE : GONE);

    if (currentEndpoint == ApiEndpoints.MOCK_MODE) {
      // Disable network proxy if we are in mock mode.
      networkProxyView.setEnabled(false);
      networkLoggingView.setEnabled(false);
    } else {
      // Disable network controls if we are not in mock mode.
      networkDelayView.setEnabled(false);
      networkVarianceView.setEnabled(false);
      networkErrorView.setEnabled(false);
    }

    // We use the JSON rest adapter as the source of truth for the log level.
    /*final EnumAdapter<LogLevel> loggingAdapter = new EnumAdapter<>(activity, LogLevel.class);
    networkLoggingView.setAdapter(loggingAdapter);
    networkLoggingView.setSelection(restAdapter.getLogLevel().ordinal());
    networkLoggingView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        LogLevel selected = loggingAdapter.getItem(position);
        if (selected != restAdapter.getLogLevel()) {
          Timber.d("Setting logging level to %s", selected);
          restAdapter.setLogLevel(selected);
        } else {
          Timber.d("Ignoring re-selection of logging level " + selected);
        }
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {
      }
    });*/
    }

  @OnClick(R.id.debug_network_endpoint_edit) void onEditEndpointClicked() {
//    Timber.d("Prompting to edit custom endpoint URL.");
    // Pass in the currently selected position since we are merely editing.
    showCustomEndpointDialog(endpointView.getSelectedItemPosition(), networkEndpoint.get());
  }

    private void setupUserInterfaceSection() {
    final AnimationSpeedAdapter speedAdapter = new AnimationSpeedAdapter(activity);
    uiAnimationSpeedView.setAdapter(speedAdapter);
    final int animationSpeedValue = animationSpeed.get();
    uiAnimationSpeedView.setSelection(
        AnimationSpeedAdapter.getPositionForValue(animationSpeedValue));
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
    }

    private void setupBuildSection() {
        buildNameView.setText(BuildConfig.VERSION_NAME);
        buildCodeView.setText(String.valueOf(BuildConfig.VERSION_CODE));
        buildShaView.setText(BuildConfig.GIT_SHA);

        try {
            // Parse ISO8601-format time into local time.
            DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            inFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date buildTime = inFormat.parse(BuildConfig.BUILD_TIME);
            buildDateView.setText(DATE_DISPLAY_FORMAT.format(buildTime));
        } catch (ParseException e) {
            throw new RuntimeException("Unable to decode build time: " + BuildConfig.BUILD_TIME, e);
        }
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

    @OnClick(R.id.debug_device_log)
    public void openLog() {
        drawerContext.startActivity(new Intent(drawerContext, LogReaderActivity.class));
    }

    @OnClick(R.id.debug_device_database_delete)
    public void deleteDatabase() {
        //.deleteDatabase(drawerContext);
        initialSetupCompleted.delete();
        Toast.makeText(drawerContext, "Database deleted. Restart app.", Toast.LENGTH_LONG).show();
        relaunch();
    }

    private void setupPicassoSection() {
        boolean picassoDebuggingValue = picassoDebugging.get();
        picasso.setDebugging(picassoDebuggingValue);
        picassoIndicatorView.setChecked(picassoDebuggingValue);
        picassoIndicatorView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                Timber.d("Setting Picasso debugging to " + isChecked);
                picasso.setDebugging(isChecked);
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
            throw new RuntimeException("Unable to apply animation speed.", e);
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
        String[] units = new String[]{"B", "KB", "MB", "GB"};
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
        Intent newApp = new Intent(app, MainActivity.class);
        newApp.setFlags(FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        app.startActivity(newApp);
        GolesApplication.get(app).buildObjectGraphAndInject();
    }
}
