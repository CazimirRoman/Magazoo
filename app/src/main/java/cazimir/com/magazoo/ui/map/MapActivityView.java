package cazimir.com.magazoo.ui.map;

import android.Manifest.permission;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimingLogger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.impl.TrelloImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import cazimir.com.magazoo.BuildConfig;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.base.BaseActivity;
import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.authentication.AuthPresenter;
import cazimir.com.magazoo.presenter.map.MapPresenter;
import cazimir.com.magazoo.reports.ReportsActivity;
import cazimir.com.magazoo.repository.Repository;
import cazimir.com.magazoo.ui.login.LoginActivityView;
import cazimir.com.magazoo.ui.tutorial.TutorialActivity;
import cazimir.com.magazoo.utils.OnErrorHandledListener;
import cazimir.com.magazoo.utils.Util;
import fr.ganfra.materialspinner.MaterialSpinner;

import static cazimir.com.magazoo.R.id.map;
import static cazimir.com.magazoo.constants.Constants.ACCURACY_TAG;
import static cazimir.com.magazoo.constants.Constants.ANA_MARIA;
import static cazimir.com.magazoo.constants.Constants.CAZIMIR;
import static cazimir.com.magazoo.constants.Constants.EVENT_ADDED;
import static cazimir.com.magazoo.constants.Constants.FARMER_MARKET;
import static cazimir.com.magazoo.constants.Constants.FASTEST_INTERVAL;
import static cazimir.com.magazoo.constants.Constants.GAS_STATION;
import static cazimir.com.magazoo.constants.Constants.LOCATION_TAG;
import static cazimir.com.magazoo.constants.Constants.SHOPPING_CENTER;
import static cazimir.com.magazoo.constants.Constants.SMALL_SHOP;
import static cazimir.com.magazoo.constants.Constants.SUPERMARKET;
import static cazimir.com.magazoo.constants.Constants.TRELLO_ACCESS_TOKEN;
import static cazimir.com.magazoo.constants.Constants.TRELLO_APP_KEY;
import static cazimir.com.magazoo.constants.Constants.TRELLO_FEEDBACK_LIST;
import static cazimir.com.magazoo.constants.Constants.UPDATE_INTERVAL;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MapActivityView extends BaseActivity implements IMapActivityView, LocationListener, OnErrorHandledListener {

    private static final String TAG = MapActivityView.class.getSimpleName();

    @BindView(R.id.shop_image)
    ImageView mShopImage;
    @BindView(R.id.shop_type_label)
    TextView mShopTypeLabel;
    @BindView(R.id.nonstop_label)
    TextView mNonStopLabel;
    @BindView(R.id.pos_label)
    TextView mPosLabel;
    @BindView(R.id.cash_only_label)
    TextView mCashOnlyLabel;
    @BindView(R.id.tickets_label)
    TextView mTicketsLabel;
    @BindView(R.id.button_report)
    BootstrapButton mButtonReport;
    @BindView(R.id.button_navigate)
    BootstrapButton mButtonNavigate;
    @BindView(R.id.shop_details)
    CardView mShopDetails;
    @BindView(R.id.shop_id)
    TextView mShopId;

    private MapPresenter mPresenter;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private GoogleMap mMap;
    private ClusterManager<Shop> mClusterManager;
    private float mCurrentAccuracy = 0;
    private LatLng mCurrentLocation;
    private Shop mCurrentSelectedShop;
    private Report mCurrentReportedShop;
    private float mCurrentZoomLevel;
    private ArrayList<Shop> mShopsInBounds;
    private MaterialDialog mReportDialog;
    private MaterialDialog mAddShopDialog;
    private MaterialDialog mFeedbackDialog;
    private MaterialDialog mAccuracyDialog;
    private MaterialDialog mLocationDialog;
    private MaterialDialog mNoGpsDialog;
    private MaterialDialog mNoInternetDialog;
    private BootstrapBrand mAddButtonBrand;
    private FrameLayout mProgress;
    private double mAddLatitude = 0.00;
    private double mAddLongitude = 0.00;
    private Drawable smallShopDetailsImage;
    private Drawable farmerMarketDetailsImage;
    private Drawable supermarkerDetailsImage;
    private Drawable hypermarkerDetailsImage;
    private Drawable gasStationDetailsImage;
    private boolean animatingToMarker = false;
    private MaterialDialog mAllowLocationDialog;
    private boolean animatingToUserLocation = false;
    private String mAdminName = "";
    private boolean mPausedForGettingAdmin = false;

    @Override
    protected void onStart() {
        registerReceiver(connectivityReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        registerReceiver(connectivityReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onStart();
    }

    private BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                if (gpsNotActive()) {
                    showNoGPSErrorDialog();
                } else {
                    closeNoGpsDialog();
                }

            } else if (intent.getAction().matches("android.net.conn.CONNECTIVITY_CHANGE")) {
                if (!networkActive()) {
                    showNoInternetErrorDialog();
                } else {
                    closeNoInternetDialog();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MapPresenter(this, new AuthPresenter(this), new Repository());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setUpMap();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    onLocationChanged(location);
                }
            }
        };

        initUI();
        setupNavigationDrawer();
    }

    private void startLocationUpdates() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, mLocationCallback,
                Looper.myLooper());
    }

    private boolean locationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void showAllowLocationDialog() {
        if (mAllowLocationDialog == null) {
            mAllowLocationDialog = Util.buildDialog(this, getString(R.string.popup_location_permission_error_title), getString(R.string.popup_location_permission_error_text), Constants.ERROR_PERMISSION).show();
            return;
        }

        mAllowLocationDialog.show();

        Log.d(TAG, "showAllowLocationDialog: called");
    }

    @Override
    protected void onStop() {
        unregisterReceiver(connectivityReceiver);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }

        closeShopDetails();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!networkActive()) {
            showNoInternetErrorDialog();
        }

        if (locationPermissionGranted()) {
            if (mFusedLocationClient != null) {
                showLocationDialog();
                getLastLocation();
                startLocationUpdates();
                if (gpsNotActive()) {
                    showNoGPSErrorDialog();
                }
            }
        } else {
            showAllowLocationDialog();
        }

        //to avoid animation breakup
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                zoomToCurrentLocation();
            }
        };

        handler.postDelayed(r, 500);

    }

    private boolean gpsNotActive() {
        return !Util.isGPSAvailable();
    }

    private boolean networkActive() {
        return Util.isNetworkAvailable();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.app_name;
    }

    private boolean isFirstRun() {

        Boolean mFirstRun;

        SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
        mFirstRun = mPreferences.getBoolean(mPresenter.getUserId(), true);
        if (mFirstRun) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(mPresenter.getUserId(), false);
            editor.apply();
            return true;
        }

        return false;
    }

    @Override
    public Report getCurrentReportedShop() {
        return mCurrentReportedShop;
    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                setMyLocationEnabled();
                setUpClusterer();
                setMapTheme();
                getMapBounds();
                setOnCameraChangeListener();
                mMap.setIndoorEnabled(false);
                mMap.setBuildingsEnabled(false);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    public boolean onMarkerClick(Marker marker) {

                        closeShopDetails();

                        if (marker.getTitle() != null) {
                            mMap.animateCamera((CameraUpdateFactory.newLatLng(marker.getPosition())));
                            animatingToMarker = true;
                            if (mShopDetails.getVisibility() == View.INVISIBLE) {
                                for (Shop shop : mShopsInBounds) {
                                    if (shop.getId() != null && shop.getId().contains(marker.getTitle())) {
                                        populateShopDetails(shop);
                                        openShopDetails();
                                        mCurrentSelectedShop = shop;
                                    }
                                }
                            }
                        }

                        return true;
                    }
                });

                refreshMarkersOnMap();
            }
        });
    }

    private void setUpClusterer() {
        mClusterManager = new ClusterManager<>(this, mMap);
        final CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, mClusterManager);
        mClusterManager.setRenderer(renderer);
        mClusterManager.setAnimation(false);

    }

    private void setupNavigationDrawer() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        if(mPresenter.isAdmin()){
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_reports).setVisible(true);
        }

        navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.nav_share) {
                    shareApplication();
                } else if (id == R.id.nav_signout) {
                    signOut();
                } else if (id == R.id.nav_signin) {
                    startLoginActivity();
                    finish();
                } else if (id == R.id.nav_tutorial) {
                    startTutorialActivity();
                } else if (id == R.id.nav_contact) {
                    showFeedbackDialog();
                } else if (id == R.id.nav_about) {
                    showAboutDialog();
                } else if (id == R.id.nav_reports){
                    startReportActivity();
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

            navigationView.getMenu().findItem(R.id.nav_signout).setVisible(true);
            View headerLayout = navigationView.getHeaderView(0);
            TextView headerText = headerLayout.findViewById(R.id.signedInUserEmail);
            headerText.setText(mPresenter.getUserEmail());
    }

    private void startReportActivity() {
        startActivity(new Intent(MapActivityView.this, ReportsActivity.class));
    }

    private void startTutorialActivity() {
        startActivity(new Intent(this, TutorialActivity.class));
    }

    private void showAboutDialog() {
        Util.buildDialog(this, getString(R.string.about), getString(R.string.application_version)
                + BuildConfig.VERSION_NAME + "\n" + getString(R.string.developed_by) + "cazimir.roman@gmail.com"
                + "\n" + getString(R.string.last_update) + new Date(String.valueOf(BuildConfig.APP_LAST_UPDATE)), 0).show();
    }

    private void shareApplication() {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = getString(R.string.share_text);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
    }

    private void showFeedbackDialog() {
        mFeedbackDialog = buildCustomDialog(getString(R.string.send_feedback), R.layout.feedback_dialog).show();
        final BootstrapEditText etFeedbackText = (BootstrapEditText) mFeedbackDialog.findViewById(R.id.editTextFeedbackText);
        BootstrapButton btnSendFeedback = (BootstrapButton) mFeedbackDialog.findViewById(R.id.buttonSendFeedback);
        btnSendFeedback.setBootstrapBrand(mAddButtonBrand);

        btnSendFeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (networkActive()) {
                    if (TextUtils.isEmpty(etFeedbackText.getText())) {
                        etFeedbackText.setError(getString(R.string.feedback_empty));
                        return;
                    }

                    sendFeedbackToTrello(etFeedbackText.getText().toString());
                } else {
                    showNoInternetErrorDialog();
                }


            }
        });

    }

    private void sendFeedbackToTrello(String feedback) {
        mFeedbackDialog.dismiss();
        new sendFeedbackToTrello().execute(feedback);
    }

    private class sendFeedbackToTrello extends AsyncTask<String, Integer, Card> {

        Trello trelloApi = new TrelloImpl(TRELLO_APP_KEY, TRELLO_ACCESS_TOKEN);

        @Override
        protected Card doInBackground(String... params) {
            Card feedBack = new Card();
            feedBack.setName(params[0]);
            feedBack.setDesc(mPresenter.getUserEmail());
            return trelloApi.createCard(TRELLO_FEEDBACK_LIST, feedBack);
        }

        @Override
        protected void onPostExecute(Card result) {
            super.onPostExecute(result);
            showToast(getString(R.string.feedback_sent));
        }
    }

    private void initUI() {
        initAddShop();
        initShopDetails();
        initBootStrapBrand();
        mProgress = findViewById(R.id.progress);

    }

    private void initBootStrapBrand() {
        mAddButtonBrand = new BootstrapBrand() {
            @Override
            public int defaultFill(Context context) {
                return context.getResources().getColor(R.color.colorPrimary);
            }

            @Override
            public int defaultEdge(Context context) {
                return context.getResources().getColor(R.color.colorPrimary);
            }

            @Override
            public int defaultTextColor(Context context) {
                return 0;
            }

            @Override
            public int activeFill(Context context) {
                return context.getResources().getColor(R.color.colorAccent);
            }

            @Override
            public int activeEdge(Context context) {
                return context.getResources().getColor(R.color.colorAccent);
            }

            @Override
            public int activeTextColor(Context context) {
                return 0;
            }

            @Override
            public int disabledFill(Context context) {
                return 0;
            }

            @Override
            public int disabledEdge(Context context) {
                return 0;
            }

            @Override
            public int disabledTextColor(Context context) {
                return 0;
            }

            @Override
            public int getColor() {
                return 0;
            }
        };
    }

    private void initAddShop() {
        FloatingActionButton fabAddShop = findViewById(R.id.fabAddShop);
        fabAddShop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "fabAddShop: clicked");
                closeShopDetails();
                showProgressBar();
                if (networkActive()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (isCorrectAccuracy()) {

                                mPresenter.checkIfAllowedToAddShop();

//                                mPresenter.checkIfAllowedToAddShop(new OnIsAllowedToAddCallback() {
//                                    @Override
//                                    public void isAllowedToAdd() {
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                showAddShopDialog();
//                                                hideProgressBar();
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void isNotAllowedToAdd() {
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                showAddLimitAlertPopup();
//                                                hideProgressBar();
//                                            }
//                                        });
//
//                                    }
//                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showAccuracyDialog();
                                    }
                                });

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideProgressBar();
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    showNoInternetErrorDialog();
                }
            }
        });
    }

    private boolean isCorrectAccuracy() {
        Log.d(TAG, "currentAccuracy: " + mCurrentAccuracy);

        if (mPresenter.getUserId().equals(ANA_MARIA)) {
            return mCurrentAccuracy != 0 && mCurrentAccuracy <= Constants.ACCURACY_DESIRED_BAM;
        }
        return mCurrentAccuracy != 0 && mCurrentAccuracy <= Constants.ACCURACY_DESIRED;

    }

    private void showAccuracyDialog() {

        if (mAccuracyDialog == null) {
            mAccuracyDialog = Util.buildCustomDialog(this, R.layout.location_dialog, true, ACCURACY_TAG).show();
            return;
        }
        mAccuracyDialog.show();
    }

    private void showLocationDialog() {
        Log.d(TAG, "showLocationDialog: called");
        if (mLocationDialog == null) {
            mLocationDialog = Util.buildCustomDialog(this, R.layout.location_dialog, false, LOCATION_TAG).show();
            return;
        }
        mLocationDialog.show();
    }


    private void showNoGPSErrorDialog() {
        if (mNoGpsDialog == null) {
            mNoGpsDialog = Util.buildCustomDialog(this, R.layout.no_gps_dialog, false, Constants.GPS_TAG).show();
            mNoGpsDialog.getCustomView().findViewById(R.id.buttonGpsSettings).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    closeNoGpsDialog();
                }
            });
        } else {
            mNoGpsDialog.show();
        }
    }

    private void showNoInternetErrorDialog() {
        hideProgressBar();
        if (mNoInternetDialog == null) {
            mNoInternetDialog = Util.buildCustomDialog(this, R.layout.no_internet_dialog, true, Constants.INTERNET_TAG).show();
            mNoInternetDialog.getCustomView().findViewById(R.id.buttonInternetSettings).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    closeNoInternetDialog();
                }
            });
        } else {
            mNoInternetDialog.show();
        }
    }

    @Override
    public void closeShopDetails() {
        mShopDetails.setVisibility(View.INVISIBLE);
    }

    @Override
    public void openShopDetails() {
        mShopDetails.setVisibility(View.VISIBLE);
    }

    private void initShopDetails() {

        if (mPresenter.getUserId().equals(CAZIMIR) || mPresenter.getUserId().equals(ANA_MARIA)) {
            mShopImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    closeShopDetails();
                    showProgressBar();
                    mPresenter.deleteShopFromDB(mCurrentSelectedShop.getId());
                    return true;
                }
            });
        }

        mButtonNavigate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToShop();
            }
        });

        mButtonReport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showReportDialog();
            }
        });

        smallShopDetailsImage = ContextCompat.getDrawable(getActivity(), R.drawable.small_shop_image);
        farmerMarketDetailsImage = ContextCompat.getDrawable(getActivity(), R.drawable.farmers_market_image);
        supermarkerDetailsImage = ContextCompat.getDrawable(getActivity(), R.drawable.supermarket_image);
        hypermarkerDetailsImage = ContextCompat.getDrawable(getActivity(), R.drawable.hypermarket_image);
        gasStationDetailsImage = ContextCompat.getDrawable(getActivity(), R.drawable.gast_station_image);
    }

    public void showReportDialog() {

        mReportDialog = buildCustomDialog(getString(R.string.popup_report_shop_title), R.layout.report_shop).show();
        BootstrapButton report_location = (BootstrapButton) mReportDialog.findViewById(R.id.button_report_location);
        BootstrapButton report_247 = (BootstrapButton) mReportDialog.findViewById(R.id.button_report_247);
        BootstrapButton report_pos = (BootstrapButton) mReportDialog.findViewById(R.id.button_report_pos);
        BootstrapButton report_tickets = (BootstrapButton) mReportDialog.findViewById(R.id.button_report_tickets);

        if (mNonStopLabel.getVisibility() == View.GONE) {
            report_247.setText(getString(R.string.popup_report_247_yes));
        } else {
            report_247.setText(getString(R.string.popup_report_247_no));
        }

        if (mPosLabel.getVisibility() == View.GONE) {
            report_pos.setText(getString(R.string.popup_report_credit_card_yes));
        } else {
            report_pos.setText(getString(R.string.popup_report_credit_card_no));
        }

        if (!inRomania(mCurrentLocation)) {

            //hiding the meal tickets report for other countries than Romania by setting width to 0.
            //set Visibility does not work for BootstrapButton. Opened an Issue on github : https://github.com/Bearded-Hen/Android-Bootstrap/issues/220
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) report_tickets.getLayoutParams();
            lp.width = 0;
            report_tickets.setLayoutParams(lp);

            if (mTicketsLabel.getVisibility() == View.GONE) {
                report_tickets.setText(getString(R.string.popup_report_tickets_yes));
            } else {
                report_tickets.setText(getString(R.string.popup_report_tickets_no));
            }
        }

        report_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeReportDialog();
                showProgressBar();
                mCurrentReportedShop = new Report(mCurrentSelectedShop.getId(), Constants.REPORT_LOCATION, false, mPresenter.getUserId(), new Date().getTime());
                mPresenter.writeReportToDatabase(mCurrentReportedShop);
            }
        });

        report_247.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeReportDialog();
                showProgressBar();
                mCurrentReportedShop = new Report(mCurrentSelectedShop.getId(), Constants.REPORT_247, !mCurrentSelectedShop.getNonstop(), mPresenter.getUserId(), new Date().getTime());
                mPresenter.writeReportToDatabase(mCurrentReportedShop);
            }
        });

        report_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeReportDialog();
                showProgressBar();
                mCurrentReportedShop = new Report(mCurrentSelectedShop.getId(), Constants.REPORT_POS, !mCurrentSelectedShop.getPos(), mPresenter.getUserId(), new Date().getTime());
                mPresenter.writeReportToDatabase(mCurrentReportedShop);
            }

        });

        report_tickets.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeReportDialog();
                showProgressBar();
                mCurrentReportedShop = new Report(mCurrentSelectedShop.getId(), Constants.REPORT_TICKETS, !mCurrentSelectedShop.getTickets(), mPresenter.getUserId(), new Date().getTime());
                mPresenter.writeReportToDatabase(mCurrentReportedShop);
            }
        });
    }

    public void showReportThanksPopup() {
        Util.buildDialog(this, getString(R.string.thanks_report), getString(R.string.details_report), 0).show();
    }

    @Override
    public void closeReportDialog() {
        //TODO: need to find a way to update the cardview without closing it
        closeShopDetails();
        mReportDialog.dismiss();
    }

    private BitmapDescriptor getIconForShop(String type) {
        Log.d(TAG, "getIconForShop: " + type);
        if (type.equals(SMALL_SHOP)) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_small_shop);
        } else if (type.equals(SUPERMARKET)) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_supermarket);
        } else if (type.equals(FARMER_MARKET)) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_farmer_market);
        } else if (type.equals(SHOPPING_CENTER)) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_shopping_center);
        } else if (type.equals(GAS_STATION)) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_gas_station);
        }

        return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_small_shop);
    }

    @Override
    public void addMarkersToMap(final ArrayList<Shop> shops) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final TimingLogger timings = new TimingLogger(TAG, "addMarkersToMap");
                Log.d(TAG, "addMarkersToMap - number of markers to add: " + shops.size());
                mClusterManager.clearItems();
                for (int i = 0; i < shops.size(); i++) {
                    mClusterManager.addItem(shops.get(i));
                }

                mClusterManager.cluster();

                mShopsInBounds = shops;
//                Log.d(TAG, "mShopsInBounds: " + mShopsInBounds.size());
//
//                for (Shop shop : mShopsInBounds) {
//                    Log.d(TAG, "ShopInBounds id is: " + shop.getId());
//                }

                timings.addSplit("done!");

                timings.dumpToLog();

                hideProgressBar();

            }
        });

    }

    public class CustomClusterRenderer extends DefaultClusterRenderer<Shop> {

        CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<Shop> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Shop shop, MarkerOptions markerOptions) {
            markerOptions.position(new LatLng(shop.getLat(), shop.getLon())).icon(getIconForShop(shop.getType())).title(shop.getId());
        }
    }

    private void navigateToShop() {
        if (mCurrentLocation != null && mCurrentSelectedShop != null) {
            final String navigationLink = "http://maps.google.com/maps?saddr="
                    .concat(String.valueOf(mCurrentLocation.latitude)).concat(", ").concat(String.valueOf(mCurrentLocation.longitude))
                    .concat("&daddr=").concat(String.valueOf(mCurrentSelectedShop.getLat())).concat(", ")
                    .concat(String.valueOf(mCurrentSelectedShop.getLon()));

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(navigationLink));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        }
    }

    private MaterialDialog.Builder buildCustomDialog(String title, int layout) {

        return new MaterialDialog.Builder(this)
                .title(title)
                .customView(layout, true);
    }

    private void getShopMarkers(final LatLngBounds bounds) {
        mPresenter.getAllMarkers(bounds);
    }

    public void showAddThanksPopup() {
        logEvent(EVENT_ADDED, null);
        Util.buildDialog(this, getString(R.string.thanks_adding_title), getString(R.string.thanks_adding_text), 0).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void signOut() {
        mPresenter.signOut();
        startLoginActivity();
        finish();
    }

    private void startLoginActivity() {
        startActivity(new Intent(MapActivityView.this, LoginActivityView.class));
    }

    private void populateShopDetails(Shop shop) {

        mShopId.setText(shop.getId());

        switch (shop.getType()) {
            case SMALL_SHOP:
                populate(smallShopDetailsImage, getString(R.string.popup_add_shop_small), shop.getNonstop(), shop.getTickets(), shop.getPos());
                break;
            case FARMER_MARKET:
                populate(farmerMarketDetailsImage, getString(R.string.popup_add_shop_farmer), shop.getNonstop(), shop.getTickets(), shop.getPos());
                break;
            case SUPERMARKET:
                populate(supermarkerDetailsImage, getString(R.string.popup_add_shop_supermarket), shop.getNonstop(), shop.getTickets(), shop.getPos());
                break;
            case SHOPPING_CENTER:
                populate(hypermarkerDetailsImage, getString(R.string.popup_add_shop_shopping_center), shop.getNonstop(), shop.getTickets(), shop.getPos());
                break;
            case GAS_STATION:
                populate(gasStationDetailsImage, getString(R.string.popup_add_gas_station), shop.getNonstop(), shop.getTickets(), shop.getPos());
                break;
        }
    }

    private void populate(Drawable shopImage, String type, boolean nonstop, boolean tickets, boolean pos) {
        mShopImage.setImageDrawable(shopImage);
        mShopTypeLabel.setText(type);

        if (nonstop) {
            mNonStopLabel.setVisibility(View.VISIBLE);
        } else {
            mNonStopLabel.setVisibility(View.GONE);
        }

        if (pos) {
            mPosLabel.setVisibility(View.VISIBLE);
            mCashOnlyLabel.setVisibility(View.GONE);
        } else {
            mPosLabel.setVisibility(View.GONE);
            mCashOnlyLabel.setVisibility(View.VISIBLE);
        }

        if (tickets) {
            mTicketsLabel.setVisibility(View.VISIBLE);
        } else {
            mTicketsLabel.setVisibility(View.GONE);
        }

    }

    private void setMapTheme() {
        try {
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }


    private void setOnCameraChangeListener() {

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int reason) {

                Log.d(TAG, "onCameraMoveStarted");

                if (animatingToMarker) {
                    return;
                }

                if (reason == REASON_GESTURE) {
                    closeShopDetails();
                }

            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                Log.d(TAG, "onCameraIdle");
                animatingToUserLocation = false;
                animatingToMarker = false;

                mClusterManager.cluster();

                setZoomLevel();

                if (!worldMapShowing()) {
                    //refreshMarkersOnMap();
                }
            }
        });
    }

    private boolean isDesiredZoomLevel() {
        return mCurrentZoomLevel == Constants.ZOOM_LEVEL_DESIRED;
    }

    @Override
    public void refreshMarkersOnMap() {
        Log.d(TAG, "refreshMarkersOnMap: called");
        getShopMarkers(getMapBounds());
    }

    @Override
    public void showProgressBar() {
        Log.d(TAG, "showProgressBar: called");
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void isAllowedToAdd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAddShopDialog();
                hideProgressBar();
            }
        });
    }

    @Override
    public void isNotAllowedToAdd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAddLimitAlertPopup();
                hideProgressBar();
            }
        });
    }

    private void setZoomLevel() {
        mCurrentZoomLevel = mMap.getCameraPosition().zoom;
    }

    private LatLngBounds getMapBounds() {
        return MapActivityView.this.mMap
                .getProjection().getVisibleRegion().latLngBounds;
    }

    private void setMyLocationEnabled() {
        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            setUpMyLocationButton();
        }
    }

    private void setUpMyLocationButton() {
        FloatingActionButton fabLocateUser = findViewById(R.id.fabLocateUser);
        fabLocateUser.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomToCurrentLocation();
            }
        });

    }

    public void zoomToCurrentLocation() {
        if (mCurrentLocation != null && !animatingToUserLocation) {
            animateToCurrentLocation();
        }
    }

    public void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    private void animateToCurrentLocation() {
        Log.d(TAG, "animateToCurrentLocation");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, Constants.ZOOM_LEVEL_DESIRED));
        animatingToUserLocation = true;
    }

    public boolean requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION}, Constants.MY_LOCATION_REQUEST_CODE);
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.MY_LOCATION_REQUEST_CODE) {
            //permission granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setMyLocationEnabled();
                zoomToCurrentLocation();
                // permission denied
            } else {
                showAllowLocationDialog();
            }
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showAddShopDialog() {

        mAddLatitude = mCurrentLocation.latitude;
        mAddLongitude = mCurrentLocation.longitude;

        Toast.makeText(this, "Shop will be added at: " + mAddLatitude + " and " + mAddLongitude, Toast.LENGTH_SHORT).show();
        if (mAddLatitude == 0.00 || mAddLongitude == 0.00) {
            Toast.makeText(this, "Latitude or longitude is null!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "showing add shop dialog");
        if (mAddShopDialog == null) {
            Log.d(TAG, "showAddShopDialog: new one");
            mAddShopDialog = buildCustomDialog(getString(R.string.popup_add_shop_title), R.layout.add_shop).build();
            final MaterialSpinner spinner = (MaterialSpinner) mAddShopDialog.findViewById(R.id.spinner_type);
            final CheckBox chkPos = (CheckBox) mAddShopDialog.findViewById(R.id.checkPos);
            final CheckBox chkNonstop = (CheckBox) mAddShopDialog.findViewById(R.id.checkNonstop);
            final CheckBox chkTickets = (CheckBox) mAddShopDialog.findViewById(R.id.checkTickets);
            BootstrapButton buttonAdd = (BootstrapButton) mAddShopDialog.findViewById(R.id.buttonAdd);
            buttonAdd.setBootstrapBrand(mAddButtonBrand);

            List<String> categories = new ArrayList<>();
            categories.add(getString(R.string.popup_add_shop_small));
            categories.add(getString(R.string.popup_add_gas_station));
            categories.add(getString(R.string.popup_add_shop_farmer));
            categories.add(getString(R.string.popup_add_shop_supermarket));
            categories.add(getString(R.string.popup_add_shop_shopping_center));

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinner.setAdapter(dataAdapter);

            buttonAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    showProgressBar();

                    if (!spinner.getSelectedItem().equals(getString(R.string.popup_add_shop_type))) {

                        final Shop shop = new Shop(Constants.ID_PLACEHOLDER, mAddLatitude, mAddLongitude, mAdminName, getShopType(spinner.getSelectedItemPosition()), chkPos.isChecked(),
                                chkNonstop.isChecked(), chkTickets.isChecked(), mPresenter.getUserId(), getShopCity(), getShopCountry());

                        closeAddShopDialog();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mPresenter.addMarkerToFirebase(shop);
                            }
                        }).start();

                    } else {
                        spinner.setError(getString(R.string.popup_add_shop_type_error));
                        hideProgressBar();
                    }
                }
            });

            mAddShopDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    resetAddShopDialog();
                }
            });
        }

        showViewIfInRomania(mAddShopDialog.getCustomView().findViewById(R.id.checkTickets));

        mAddShopDialog.show();
    }

    private void showViewIfInRomania(View view) {

        if (inRomania(mCurrentLocation)) {
            Log.d(TAG, "Country is Romania");
            view.setVisibility(View.VISIBLE);
        }
    }

    private String getShopType(int selectedItem) {

        Log.d(TAG, "getShopType: selectedItem " + selectedItem);

        switch (selectedItem) {
            case 1:
                return SMALL_SHOP;
            case 2:
                return GAS_STATION;
            case 3:
                return FARMER_MARKET;
            case 4:
                return SUPERMARKET;
            case 5:
                return SHOPPING_CENTER;
        }

        //should not reach this because validation is done before this method is called
        return "";
    }

    private String getShopCountry() {
        Geocoder gcd = new Geocoder(this, Locale.US);
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(mCurrentLocation.latitude, mCurrentLocation.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            return (addresses.get(0).getCountryName());
        }

        return "";
    }

    private String getShopCity() {
        Geocoder gcd = new Geocoder(this, Locale.US);
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(mCurrentLocation.latitude, mCurrentLocation.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            return (addresses.get(0).getLocality());
        }

        return "";
    }

    @Override
    public void closeAddShopDialog() {
        mAddShopDialog.dismiss();
    }

    private void resetAddShopDialog() {
        Spinner spinner;
        if (mAddShopDialog.getCustomView() != null) {
            spinner = mAddShopDialog.getCustomView().findViewById(R.id.spinner_type);
            spinner.setSelection(0);
        }

        CheckBox checkPos = mAddShopDialog.getCustomView().findViewById(R.id.checkPos);
        checkPos.setChecked(false);
        CheckBox checkTickets = mAddShopDialog.getCustomView().findViewById(R.id.checkTickets);
        checkTickets.setChecked(false);
        CheckBox checkNonstop = mAddShopDialog.getCustomView().findViewById(R.id.checkNonstop);
        checkNonstop.setChecked(false);
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "Got new location: " + location);

        mCurrentAccuracy = location.getAccuracy();
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        //Toast.makeText(this, "Current accuracy is: " + mCurrentAccuracy + " meters.", Toast.LENGTH_SHORT).show();

        if (mLocationDialog != null && mLocationDialog.isShowing() && mCurrentAccuracy > 0 && isDesiredZoomLevel()) {
            mLocationDialog.dismiss();
        }

        if (mAccuracyDialog != null && mAccuracyDialog.isShowing() && isCorrectAccuracy()) {
            mAccuracyDialog.dismiss();
            Log.d(TAG, "Dismissing accuracy dialog and showing addshopdialog");
            showAddShopDialog();
        }

        if (worldMapShowing()) {
            zoomToCurrentLocation();
        } else {
            updateCameraBearing(mMap, location.getBearing());
        }

    }

    private void updateCameraBearing(GoogleMap mMap, float bearing) {

        if(!animatingToUserLocation){
            if (mMap == null) return;
            CameraPosition camPos = CameraPosition
                    .builder(
                            mMap.getCameraPosition()
                    )
                    .bearing(bearing)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
        }
    }

    private void closeNoGpsDialog() {
        if (mNoGpsDialog != null && mNoGpsDialog.isShowing()) {
            mNoGpsDialog.dismiss();
        }
    }

    private void closeNoInternetDialog() {
        if (mNoInternetDialog != null && mNoInternetDialog.isShowing()) {
            mNoInternetDialog.dismiss();
        }
    }

    private boolean worldMapShowing() {

        float zoom = mMap.getCameraPosition().zoom;
        Log.d(TAG, "worldMapShowing: " + String.valueOf(zoom >= 2.0 && zoom < 2.1));
        Log.d(TAG, "zoom: " + mMap.getCameraPosition().zoom);
        return zoom >= 2.0 && zoom < 2.1;
    }

    private void showAddLimitAlertPopup() {
        Util.buildDialog(this, getString(R.string.popup_shop_limit_error_title), getString(R.string.popup_shop_limit_error_text), Constants.ERROR_LIMIT).show();
    }

    @Override
    public IGeneralView getInstance() {
        return this;
    }

    @Override
    public Activity getActivity() {
        return this;
    }
}
