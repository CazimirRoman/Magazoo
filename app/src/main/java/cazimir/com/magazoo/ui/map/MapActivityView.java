package cazimir.com.magazoo.ui.map;

import android.Manifest.permission;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
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

import cazimir.com.magazoo.BuildConfig;
import cazimir.com.magazoo.R;
import cazimir.com.magazoo.base.BaseActivity;
import cazimir.com.magazoo.base.IGeneralView;
import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.map.MapPresenter;
import cazimir.com.magazoo.ui.login.LoginActivityView;
import cazimir.com.magazoo.ui.tutorial.TutorialActivity;
import cazimir.com.magazoo.utils.OnErrorHandledListener;
import cazimir.com.magazoo.utils.Util;
import fr.ganfra.materialspinner.MaterialSpinner;

import static cazimir.com.magazoo.R.id.map;
import static cazimir.com.magazoo.constants.Constants.ACCURACY_TAG;
import static cazimir.com.magazoo.constants.Constants.TRELLO_ACCESS_TOKEN;
import static cazimir.com.magazoo.constants.Constants.TRELLO_APP_KEY;
import static cazimir.com.magazoo.constants.Constants.TRELLO_FEEDBACK_LIST;
import static cazimir.com.magazoo.constants.Constants.WORLD_MAP_TAG;

public class MapActivityView extends BaseActivity implements IMapActivityView, LocationListener, OnErrorHandledListener {

    private static final String TAG = MapActivityView.class.getSimpleName();

    private MapPresenter mPresenter;

    private GoogleMap mMap;
    private ClusterManager<Shop> mClusterManager;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private float mCurrentAccuracy = 0;
    private LatLng mCurrentLocation;
    private LatLng mCurrentOpenShopLatLng;
    private Shop mCurrentSelectedShop;
    private Report mCurrentReportedShop;
    private float mCurrentZoomLevel;
    private ArrayList<Shop> mShopsInBounds;
    private CardView mShopDetails;
    private TextView mShopTypeLabel;
    private TextView mNonStopLabel;
    private TextView mPosLabel;
    private TextView mCashOnlyLabel;
    private TextView mTicketsLabel;
    private MaterialDialog mReportDialog;
    private MaterialDialog mAddShopDialog;
    private MaterialDialog mFeedbackDialog;
    private ImageView mShopImage;
    private MaterialDialog mAccuracyDialog;
    private MaterialDialog mNoGpsDialog;
    private MaterialDialog mNoInternetDialog;
    private BootstrapBrand mAddButtonBrand;
    private FrameLayout mProgress;
    private double mAddLatitude;
    private double mAddLongitude;

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        registerReceiver(connectivityReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        registerReceiver(connectivityReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        super.onStart();
    }

    private BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                if (!gpsActive()) {
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
        mPresenter = new MapPresenter(this);
        setupApiClientLocation();
        setUpMap();
        checkIfOnboardingNeeded();
        initUI();
        setupNavigationDrawer();
        showLocationDialog(WORLD_MAP_TAG, false);

    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        unregisterReceiver(connectivityReceiver);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gpsActive()) {
            showNoGPSErrorDialog();
        }

        if (!networkActive()) {
            showNoInternetErrorDialog();
        }
    }

    private boolean gpsActive() {
        return Util.isGPSAvailable();
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

    private void checkIfOnboardingNeeded() {

        if (isFirstRun()) {
            startTutorialActivity();
        }
    }

    private void startTutorialActivity() {
        startActivity(new Intent(this, TutorialActivity.class));
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

    private void setupApiClientLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(MapActivityView.this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                    mGoogleApiClient);

                            LocationServices.FusedLocationApi.requestLocationUpdates(
                                    mGoogleApiClient, mLocationRequest, MapActivityView.this);
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                setUpClusterer();
                setMapTheme();
                getMapBounds();
                setMyLocationEnabled();
                setOnCameraChangeListener();
                mMap.setIndoorEnabled(false);
                mMap.setBuildingsEnabled(false);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                        if (marker.getTitle() != null) {
                            mMap.animateCamera((CameraUpdateFactory.newLatLngZoom(marker.getPosition(), Constants.ZOOM_LEVEL_DESIRED)));
                            if (mShopDetails.getVisibility() == View.GONE) {
                                for (Shop shop : mShopsInBounds) {
                                    if (shop.getId() != null && shop.getId().contains(marker.getTitle())) {
                                        populateShopDetails(shop);
                                        openShopDetails();
                                        getAddress(new LatLng(shop.getLat(), shop.getLon()));
                                        mCurrentSelectedShop = shop;
                                    }
                                }
                            }
                        } else {
                            //do nothing, a cluster has been clicked
                        }

                        return true;
                    }
                });
            }
        });
    }

    private void setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<Shop>(this, mMap);
        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<Shop>() {
            @Override
            public boolean onClusterClick(Cluster<Shop> cluster) {
                return false;
            }
        });

        //mMap.setOnMarkerClickListener(mClusterManager);

        final CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, mClusterManager);
        mClusterManager.setRenderer(renderer);

    }

    private void getAddress(LatLng location) {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.latitude, location.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            Log.d(TAG, addresses.get(0).getLocality());
        } else {
            // do your stuff
        }
    }

    private void setupNavigationDrawer() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
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
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        if (mPresenter.isUserLoggedIn()) {
            navigationView.getMenu().findItem(R.id.nav_signout).setVisible(true);
            View headerLayout = navigationView.getHeaderView(0);
            TextView headerText = headerLayout.findViewById(R.id.signedInUserEmail);
            headerText.setText(mPresenter.getUserEmail());
        } else {
            navigationView.getMenu().findItem(R.id.nav_signin).setVisible(true);
        }
    }

    private void showAboutDialog() {
        Util.buildDialog(this, getString(R.string.about), getString(R.string.application_version) + BuildConfig.VERSION_NAME + "\n" + getString(R.string.developed_by) + "cazimir.roman@gmail.com" + "\n" + getString(R.string.last_update) + BuildConfig.APP_LAST_UPDATE, 0).show();
    }

    private void shareApplication() {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = getString(R.string.share_text);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
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

                mAddLatitude = mCurrentLocation.latitude;
                mAddLongitude = mCurrentLocation.longitude;

                if (networkActive()) {
                    registerListenerForNewMarkerAdded();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (correctAccuracy()) {
                                mPresenter.checkIfAllowedToAdd(new OnIsAllowedToAddListener() {
                                    @Override
                                    public void isAllowedToAdd() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showAddShopDialog();
                                            }
                                        });
                                    }

                                    @Override
                                    public void isNotAllowedToAdd() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                showAddLimitAlertPopup();
                                            }
                                        });

                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showLocationDialog(ACCURACY_TAG, true);
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

    private boolean correctAccuracy() {
        Log.d(TAG, "currentAccuracy: " + mCurrentAccuracy);
        return mCurrentAccuracy != 0 && mCurrentAccuracy <= Constants.ACCURACY_DESIRED;

    }

    private void showLocationDialog(String tag, boolean isCancelable) {
        mAccuracyDialog = Util.buildCustomDialog(this, R.layout.location_dialog, isCancelable, tag).show();
        Log.d(TAG, "mAccuracy dialog is showing: " + mAccuracyDialog.isShowing());
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
        mShopDetails.setVisibility(View.GONE);
    }

    @Override
    public void openShopDetails() {
        mShopDetails.setVisibility(View.VISIBLE);
    }

    private void initShopDetails() {
        mShopDetails = findViewById(R.id.shop_details);
        mShopTypeLabel = mShopDetails.findViewById(R.id.shop_type_label);
        mNonStopLabel = mShopDetails.findViewById(R.id.nonstop_label);
        mPosLabel = mShopDetails.findViewById(R.id.pos_label);
        mCashOnlyLabel = mShopDetails.findViewById(R.id.cash_only_label);
        mTicketsLabel = mShopDetails.findViewById(R.id.tickets_label);
        mShopImage = mShopDetails.findViewById(R.id.shop_image);

        BootstrapButton buttonNavigate = mShopDetails.findViewById(R.id.button_navigate);
        BootstrapButton buttonReport = mShopDetails.findViewById(R.id.button_report);
        BootstrapButton buttonDeleteShop = mShopDetails.findViewById(R.id.button_delete);
        buttonDeleteShop.setVisibility(View.GONE);

        if (mPresenter.getUserId().equals("cJEabMRtfLc6h5fHxSuJpJegnNE3") || mPresenter.getUserId().equals("0nErC13lEHfdGcrSyZNJiNyIUHk2")) {
            buttonDeleteShop.setVisibility(View.VISIBLE);
        }

        buttonNavigate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToShop();
            }
        });

        buttonReport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showReportDialog();
            }
        });

        buttonDeleteShop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeShopDetails();
                mPresenter.deleteShopFromDB(mCurrentSelectedShop.getId());
            }
        });
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

        if (mTicketsLabel.getVisibility() == View.GONE) {
            report_tickets.setText(getString(R.string.popup_report_tickets_yes));
        } else {
            report_tickets.setText(getString(R.string.popup_report_tickets_no));
        }

        report_location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeReportDialog();
                showProgressBar();
                mCurrentReportedShop = new Report(mCurrentSelectedShop.getId(), Constants.REPORT_LOCATION, false, mPresenter.getUserId(), new Date().getTime());
                mPresenter.checkIfDuplicateReport(mCurrentReportedShop);
            }
        });

        report_247.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeReportDialog();
                showProgressBar();
                mCurrentReportedShop = new Report(mCurrentSelectedShop.getId(), Constants.REPORT_247, !mCurrentSelectedShop.getNonstop(), mPresenter.getUserId(), new Date().getTime());
                mPresenter.checkIfDuplicateReport(mCurrentReportedShop);
            }
        });

        report_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeReportDialog();
                showProgressBar();
                mCurrentReportedShop = new Report(mCurrentSelectedShop.getId(), Constants.REPORT_POS, !mCurrentSelectedShop.getPos(), mPresenter.getUserId(), new Date().getTime());
                mPresenter.checkIfDuplicateReport(mCurrentReportedShop);
            }

        });

        report_tickets.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeReportDialog();
                showProgressBar();
                mCurrentReportedShop = new Report(mCurrentSelectedShop.getId(), Constants.REPORT_TICKETS, !mCurrentSelectedShop.getTickets(), mPresenter.getUserId(), new Date().getTime());
                mPresenter.checkIfDuplicateReport(mCurrentReportedShop);
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

    @Override
    public void addNewlyAddedMarkerToMap(Shop shop, String title) {
        mClusterManager.addItem(shop);
        populateShopDetails(shop);
    }

    private BitmapDescriptor getIconForShop(String type) {

        if (type.equals(getString(R.string.popup_add_shop_small))) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_small_shop);
        } else if (type.equals(getString(R.string.popup_add_shop_supermarket))) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_supermarket);
        } else if (type.equals(getString(R.string.popup_add_shop_farmer))) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_farmer_market);
        } else if (type.equals(getString(R.string.popup_add_shop_shopping_center))) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_hypermarket);
        } else if (type.equals(getString(R.string.popup_add_shop_hypermarket))) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_hypermarket);
        } else if(type.equals(getString(R.string.popup_add_gas_station))) {
            return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_gas_station);
        }

        return BitmapDescriptorFactory.fromResource(R.drawable.ic_icon_small_shop);
    }

    @Override
    public void addMarkersToMap(ArrayList<Shop> shops) {
        Log.d(TAG, "addMarkersToMap - number of shops to add: " + shops.size());
        for (int i = 0; i < shops.size(); i++) {
            mClusterManager.addItem(shops.get(i));
        }

        mShopsInBounds = shops;
        mClusterManager.cluster();
        Log.d(TAG, "mShopsInBounds: " + mShopsInBounds.size());
    }

    public class CustomClusterRenderer extends DefaultClusterRenderer<Shop> {

        public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<Shop> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(Shop item, MarkerOptions markerOptions) {
            markerOptions.position(new LatLng(item.getLat(), item.getLon())).icon(getIconForShop(item.getType())).title(item.getId());
        }
    }

    private void navigateToShop() {
        if (mCurrentLocation != null && mCurrentOpenShopLatLng != null) {
            final String navigationLink = "http://maps.google.com/maps?saddr="
                    .concat(String.valueOf(mCurrentLocation.latitude)).concat(", ").concat(String.valueOf(mCurrentLocation.longitude))
                    .concat("&daddr=").concat(String.valueOf(mCurrentOpenShopLatLng.latitude)).concat(", ")
                    .concat(String.valueOf(mCurrentOpenShopLatLng.longitude));

            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
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

    private void registerListenerForNewMarkerAdded() {
        mPresenter.addListenerForNewMarkerAdded();
    }

    private void getShopMarkers(LatLngBounds bounds) {
        mPresenter.getAllMarkers(bounds);
    }

    public void showAddThanksPopup() {
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
        mCurrentOpenShopLatLng = new LatLng(shop.getLat(), shop.getLon());

        if (shop.getType().equals(getString(R.string.popup_add_shop_hypermarket))) {
            mShopTypeLabel.setText(getString(R.string.popup_add_shop_shopping_center));
        }

        mShopTypeLabel.setText(shop.getType());

        if (shop.getType().equals(getString(R.string.popup_add_shop_small))) {
            mShopImage.setImageDrawable(getResources().getDrawable(R.drawable.small_shop_image));
        } else if (shop.getType().equals(getString(R.string.popup_add_shop_farmer))) {
            mShopImage.setImageDrawable(getResources().getDrawable(R.drawable.farmers_market_image));
        } else if (shop.getType().equals(getString(R.string.popup_add_shop_supermarket))) {
            mShopImage.setImageDrawable(getResources().getDrawable(R.drawable.supermarket_image));
        } else if (shop.getType().equals(getString(R.string.popup_add_shop_hypermarket))) {
            mShopImage.setImageDrawable(getResources().getDrawable(R.drawable.hypermarket_image));
        } else if(shop.getType().equals(getString(R.string.popup_add_gas_station))){
            mShopImage.setImageDrawable(getResources().getDrawable(R.drawable.gast_station_image));
        }

        if (shop.getNonstop()) {
            mNonStopLabel.setVisibility(View.VISIBLE);
        } else {
            mNonStopLabel.setVisibility(View.GONE);
        }

        if (shop.getPos()) {
            mPosLabel.setVisibility(View.VISIBLE);
            mCashOnlyLabel.setVisibility(View.GONE);
        } else {
            mPosLabel.setVisibility(View.GONE);
            mCashOnlyLabel.setVisibility(View.VISIBLE);
        }

        if (shop.getTickets()) {
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
            public void onCameraMoveStarted(int i) {
                closeShopDetails();
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                setZoomLevel();

                if (!worldMapShowing()) {
                    refreshMarkersOnMap();
                }
            }
        });
    }

    private boolean isDesiredZoomLevel() {
        return mCurrentZoomLevel <= Constants.ZOOM_LEVEL_DESIRED;
    }

    @Override
    public void refreshMarkersOnMap() {
        mClusterManager.clearItems();
        getShopMarkers(getMapBounds());
    }

    @Override
    public void showProgressBar() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mProgress.setVisibility(View.INVISIBLE);
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
            zoomToCurrentLocation();
        } else {
            requestLocationPermissions();
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
        if (mCurrentLocation != null) {
            animateToCurrentLocation();
        } else {
            if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (location != null) {
                    mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    animateToCurrentLocation();
                }
            }
        }
    }

    private void animateToCurrentLocation() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, Constants.ZOOM_LEVEL_DESIRED));
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setMyLocationEnabled();
            } else {
                Util.buildDialog(this, getString(R.string.popup_location_permission_error_title), getString(R.string.popup_location_permission_error_text), Constants.ERROR_PERMISSION).show();
            }
        }
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showAddShopDialog() {
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
                if (!spinner.getSelectedItem().equals(getString(R.string.popup_add_shop_type))) {
                    closeAddShopDialog();
                    mPresenter.addMarkerToFirebase(new Shop(Constants.ID_PLACEHOLDER, mAddLatitude, mAddLongitude,
                            spinner.getSelectedItem().toString(), chkPos.isChecked(),
                            chkNonstop.isChecked(), chkTickets.isChecked(), mPresenter.getUserId(), getShopCity(), getShopCountry()));
                    Log.d(TAG, "addMarkerToFirebase: " + "User who added this shop is: " + mPresenter.getUserId());
                    showProgressBar();
                } else {
                    spinner.setError(getString(R.string.popup_add_shop_type_error));
                }

            }
        });

        mAddShopDialog.show();
    }

    private String getShopCountry() {
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
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
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
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

    @Override
    public void onLocationChanged(Location location) {

        if (mAccuracyDialog != null && mAccuracyDialog.isShowing() && isDesiredZoomLevel() && mAccuracyDialog.getTag().equals(WORLD_MAP_TAG)) {
            mAccuracyDialog.dismiss();
        }

        if (mAccuracyDialog != null && mAccuracyDialog.isShowing() && isDesiredZoomLevel() && mAccuracyDialog.getTag().equals(ACCURACY_TAG)) {
            mAccuracyDialog.dismiss();
            showAddShopDialog();
        }

        if (worldMapShowing()) {
            zoomToCurrentLocation();
        }

        mCurrentAccuracy = location.getAccuracy();
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
        Log.d(TAG, "worldMapShowing: " + String.valueOf(mMap.getCameraPosition().zoom == 2.0));
        return mMap.getCameraPosition().zoom == 2.0;
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
