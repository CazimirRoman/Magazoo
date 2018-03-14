package magazoo.magazine.langa.tine.ui.map;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.NetworkUtils;
import com.facebook.login.LoginManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.base.BaseActivity;
import magazoo.magazine.langa.tine.base.IGeneralView;
import magazoo.magazine.langa.tine.constants.Constants;
import magazoo.magazine.langa.tine.model.Marker;
import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.presenter.MapPresenter;
import magazoo.magazine.langa.tine.ui.login.LoginActivityView;
import magazoo.magazine.langa.tine.ui.profile.ProfileActivity;
import magazoo.magazine.langa.tine.ui.tutorial.TutorialActivity;
import magazoo.magazine.langa.tine.utils.OnErrorHandledListener;
import magazoo.magazine.langa.tine.utils.Util;

import static magazoo.magazine.langa.tine.R.id.map;

public class MapActivityView extends BaseActivity implements IMapActivityView, OnNavigationItemSelectedListener, OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener, OnErrorHandledListener, OnIsAllowedToAddListener, OnIsAllowedToReportListener {

    private static final String TAG = MapActivityView.class.getSimpleName();

    private MapPresenter mPresenter;

    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private float mCurrentAccuracy = 0;
    private LatLng mCurrentLocation;
    private LatLng mCurrentOpenShopLatLng;
    private Marker mCurrentOpenShop;
    private Report mCurrentReportedShop;
    private float mCurrentZoomLevel;
    private LatLngBounds mBounds;
    private ArrayList<Marker> mMarkersInBounds;
    private CardView mShopDetails;
    private TextView mShopTypeLabel;
    private TextView mNonStopLabel;
    private TextView mPosLabel;
    private TextView mTicketsLabel;
    private MapActivityView mContext;
    private MaterialDialog mReportDialog;
    private MaterialDialog mAddShopDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MapPresenter(this);
        mAuth = FirebaseAuth.getInstance();
        onboardingNeeded();
        initUI();
        setupNavigationDrawer();
        setUpMap();
        setupApiClientLocation();
        createLocationRequest();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int setActionBarTitle() {
        return R.string.app_name;
    }

    private void onboardingNeeded() {

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
        mFirstRun = mPreferences.getBoolean(mAuth.getCurrentUser().getUid(), true);
        if (mFirstRun) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(mAuth.getCurrentUser().getUid(), false);
            editor.apply();
            return true;
        }

        return false;
    }

    public Marker getCurrentOpenShop() {
        return mCurrentOpenShop;
    }

    private void checkInternetConnection() {
        if (!NetworkUtils.isConnected()) {
            showNoInternetErrorDialog();
        }
    }

    private void checkGPSConnection() {
        if (!Util.isGPSAvailable()) {
            showNoGPSErrorDialog();
        }
    }

    private void setupApiClientLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);

        mapFragment.getMapAsync(this);
    }

    private void setupNavigationDrawer() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, getToolbar(), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (mPresenter.isUserLoggedIn()) {
            navigationView.getMenu().findItem(R.id.nav_signout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
            View headerLayout = navigationView.getHeaderView(0);
            TextView headerText = headerLayout.findViewById(R.id.signedInUserEmail);
            headerText.setText(mPresenter.getUserEmail());
        } else {
            navigationView.getMenu().findItem(R.id.nav_signin).setVisible(true);
        }
    }

    private void initUI() {

        //floating button for adding shops
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (mCurrentAccuracy != 0 && mCurrentAccuracy <= Constants.ACCURACY_DESIRED) {
                        if (mPresenter.isUserLoggedIn()) {
                            mPresenter.checkIfAllowedToAdd(MapActivityView.this);
                        } else {
                            startLoginActivity();
                            finish();
                        }
                    } else {
                        showAccuracyErrorDialog();
                    }

            }
        });

        // cardview for shop details
        initUIShopDetails();

    }

    private void showAccuracyErrorDialog() {
        showErrorDialog(getString(R.string.popup_accuracy_error_title), getString(R.string.popup_accuracy_error_text) + "\n" + getString(R.string.popup_current_accuracy) + " " + mCurrentAccuracy, Constants.ERROR_ACCURACY);
    }

    @Override
    public void showShopLimitErrorDialog() {

    }

    @Override
    public void closeShopDetails() {
        mShopDetails.setVisibility(View.GONE);
    }

    public void showDuplicateReportErrorDialog(String regards) {
        showErrorDialog(String.format(getString(R.string.popup_report_duplicate_error_title), regards), String.format(getString(R.string.popup_report_duplicate_error_text), regards), Constants.ERROR_LIMIT);
    }

    private void showNoInternetErrorDialog() {
        showErrorDialog(getString(R.string.popup_connection_error_title), getString(R.string.popup_connection_error_text), Constants.ERROR_INTERNET);
    }

    private void showNoGPSErrorDialog() {
        showErrorDialog(getString(R.string.popup_gps_error_title), getString(R.string.popup_gps_error_text), Constants.ERROR_LOCATION);
    }

    private void initUIShopDetails() {
        mShopDetails = findViewById(R.id.shop_details);
        mShopTypeLabel = mShopDetails.findViewById(R.id.shop_type_label);
        mNonStopLabel = mShopDetails.findViewById(R.id.nonstop_label);
        mPosLabel = mShopDetails.findViewById(R.id.pos_label);
        mTicketsLabel = mShopDetails.findViewById(R.id.tickets_label);

        ImageButton buttonNavigate = mShopDetails.findViewById(R.id.button_navigate);
        ImageButton buttonReport = mShopDetails.findViewById(R.id.button_report);

        buttonNavigate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToShop();
            }
        });

        buttonReport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPresenter.isUserLoggedIn()) {
                    mPresenter.checkIfAllowedToReport(MapActivityView.this);
                } else {
                    startLoginActivity();
                    finish();
                }
            }
        });
    }

    public void showReportDialog() {

        mReportDialog = buildCustomDialog(getString(R.string.popup_report_shop_title), R.layout.report_shop).show();
        Button report_location = (Button) mReportDialog.findViewById(R.id.button_report_location);
        Button report_247 = (Button) mReportDialog.findViewById(R.id.button_report_247);
        Button report_pos = (Button) mReportDialog.findViewById(R.id.button_report_pos);
        Button report_tickets = (Button) mReportDialog.findViewById(R.id.button_report_tickets);

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
                mCurrentReportedShop = new Report(mCurrentOpenShop.getId(), Constants.REPORT_LOCATION, false, mAuth.getCurrentUser().getUid(), new Date().getTime());
                mPresenter.checkIfDuplicateReport(mCurrentReportedShop);
            }


        });

        report_247.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentReportedShop = new Report(mCurrentOpenShop.getId(), Constants.REPORT_247, !mCurrentOpenShop.getNonstop(), mAuth.getCurrentUser().getUid(), new Date().getTime());
                mPresenter.checkIfDuplicateReport(mCurrentReportedShop);
            }
        });

        report_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentReportedShop = new Report(mCurrentOpenShop.getId(), Constants.REPORT_POS, !mCurrentOpenShop.getPos(), mAuth.getCurrentUser().getUid(), new Date().getTime());
                mPresenter.checkIfDuplicateReport(mCurrentReportedShop);
            }

        });

        report_tickets.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentReportedShop = new Report(mCurrentOpenShop.getId(), Constants.REPORT_TICKETS, !mCurrentOpenShop.getTickets(), mAuth.getCurrentUser().getUid(), new Date().getTime());
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
        mShopDetails.setVisibility(View.GONE);
        mReportDialog.dismiss();
    }

    @Override
    public void addNewlyAddedMarkerToMap(Marker marker, String title) {

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(marker.getLat(), marker.getLon()))
                .title(title));
    }

    @Override
    public void addMarkersToMap(ArrayList<Marker> markers) {
        mMap.clear();
        for (int i = 0; i < markers.size(); i++) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(markers.get(i).getLat(), markers.get(i).getLon()))
                    .title(markers.get(i).getId()));
        }

        mMarkersInBounds = markers;
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

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.INTERVAL);
        mLocationRequest.setFastestInterval(Constants.FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private MaterialDialog.Builder buildCustomDialog(String title, int layout) {

        return new MaterialDialog.Builder(this)
                .title(title)
                .customView(layout, true);
    }

    private void onNewShopMarkerAdded() {
        mPresenter.addListenerForNewMarkerAdded();
    }

    private void getShopMarkers() {
        mPresenter.getAllMarkers(mBounds);
    }

    public void showAddThanksPopup() {
        Util.buildDialog(this, getString(R.string.thanks_adding), getString(R.string.details_adding), 0).show();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startProfileActivity();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_signout) {
            signOut();
        } else if (id == R.id.nav_signin) {
            startLoginActivity();
            finish();
        } else if (id == R.id.nav_tutorial) {
            startTutorialActivity();
        } else if (id == R.id.nav_contact) {
            sendContactEmail();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startProfileActivity() {
        startActivity(new Intent(MapActivityView.this, ProfileActivity.class));
    }

    private void sendContactEmail() {
        Intent feedbackEmail = new Intent(Intent.ACTION_SEND);
        feedbackEmail.setType("text/email");
        feedbackEmail.putExtra(Intent.EXTRA_EMAIL, new String[] {"cazimir.developer@gmail.com"});
        feedbackEmail.putExtra(Intent.EXTRA_SUBJECT, mAuth.getCurrentUser().getEmail() + " has left a feedback");
        startActivity(Intent.createChooser(feedbackEmail, "Send Feedback:"));
    }

    private void signOut() {

        mAuth.signOut();
        LoginManager.getInstance().logOut();
        startLoginActivity();
        finish();

    }

    private void startLoginActivity() {
        startActivity(new Intent(MapActivityView.this, LoginActivityView.class));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

                mMap.animateCamera((CameraUpdateFactory.newLatLngZoom(marker.getPosition(), Constants.ZOOM_LEVEL_DESIRED)));
                if (mShopDetails.getVisibility() == View.GONE) {
                    for (Marker d : mMarkersInBounds) {
                        if (d.getId() != null && d.getId().contains(marker.getTitle())) {
                            showShopDetails(d);
                        }
                    }
                }

                return true;
            }
        });

        setMapTheme();
        getMapBounds();
        setMyLocationEnabled();
        setOnCameraChangeListener();
        if (mCurrentZoomLevel > 1 && mCurrentZoomLevel >= Constants.ZOOM_LEVEL_DESIRED) {
            getShopMarkers();
        }

    }

    private void showShopDetails(Marker marker) {

        mCurrentOpenShop = marker;
        mCurrentOpenShopLatLng = new LatLng(marker.getLat(), marker.getLon());
        mShopTypeLabel.setText(marker.getType());

        if (marker.getNonstop()) {
            mNonStopLabel.setVisibility(View.VISIBLE);
        } else {
            mNonStopLabel.setVisibility(View.GONE);
        }

        if (marker.getPos()) {
            mPosLabel.setVisibility(View.VISIBLE);
        } else {
            mPosLabel.setVisibility(View.GONE);
        }

        if (marker.getTickets()) {
            mTicketsLabel.setVisibility(View.VISIBLE);
        } else {
            mTicketsLabel.setVisibility(View.GONE);
        }

        mShopDetails.setVisibility(View.VISIBLE);
    }

    private void setMapTheme() {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
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
                mShopDetails.setVisibility(View.GONE);
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                setZoomLevel();
                if (mCurrentZoomLevel > 1 && mCurrentZoomLevel >= Constants.ZOOM_LEVEL_DESIRED) {
                    mMap.clear();
                    getMapBounds();
                    getShopMarkers();
                    onNewShopMarkerAdded();
                } else {
                    //first run only
                    if (mCurrentZoomLevel != 2) {
                        Util.buildDialog(MapActivityView.this, "Max zoom reached", "Max zoom", Constants.ERROR_MAX_ZOOM).show();
                    }
                }
            }
        });
    }

    private void setZoomLevel() {
        mCurrentZoomLevel = mMap.getCameraPosition().zoom;
    }

    private void getMapBounds() {
        mBounds = MapActivityView.this.mMap
                .getProjection().getVisibleRegion().latLngBounds;
    }

    private void setMyLocationEnabled() {

        if (ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            zoomToCurrentLocation();
        } else {
            requestLocationPermissions();
        }

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
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng marker = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, Constants.ZOOM_LEVEL_DESIRED));
            }

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void showAddShopDialog() {

        mAddShopDialog = buildCustomDialog(getString(R.string.popup_add_shop_title), R.layout.add_shop).show();
        final MaterialSpinner spinner = (MaterialSpinner) mAddShopDialog.findViewById(R.id.spinner_type);
        final CheckBox chkPos = (CheckBox) mAddShopDialog.findViewById(R.id.checkPos);
        final CheckBox chkNonstop = (CheckBox) mAddShopDialog.findViewById(R.id.checkNonstop);
        final CheckBox chkTickets = (CheckBox) mAddShopDialog.findViewById(R.id.checkTickets);
        final EditText editDescription = (EditText) mAddShopDialog.findViewById(R.id.editDescription);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MapActivityView.this, "selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<String> categories = new ArrayList<>();
        categories.add(getString(R.string.popup_add_shop_type_small));
        categories.add(getString(R.string.popup_add_shop_farmer));
        categories.add(getString(R.string.popup_add_shop_supermarket));
        categories.add(getString(R.string.popup_add_shop_type_hypermarket));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        Button buttonAdd = (Button) mAddShopDialog.findViewById(R.id.buttonAdd);
        Button buttonCancel = (Button) mAddShopDialog.findViewById(R.id.buttonCancel);

        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddShopDialog.dismiss();
            }
        });

        buttonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!spinner.getSelectedItem().equals(getString(R.string.popup_add_shop_type))) {

                    mPresenter.addMarkerToFirebase(new Marker(Constants.ID_PLACEHOLDER, "name", mCurrentLocation.latitude, mCurrentLocation.longitude,
                            spinner.getSelectedItem().toString(), chkPos.isChecked(),
                            chkNonstop.isChecked(), chkTickets.isChecked(), editDescription.getText().toString(), 0.00, ""));
                } else {
                    spinner.setError(getString(R.string.popup_add_shop_type));
                }

            }
        });
    }

    @Override
    public void closeAddShopDialog() {
        mAddShopDialog.dismiss();
    }

    @Override
    public void showErrorDialog(String title, String message, int errorType) {
        Util.buildDialog(this, title, message, errorType).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentAccuracy = location.getAccuracy();
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void isAllowedToReport() {
        showReportDialog();
    }

    @Override
    public void isNotAllowedToReport() {
        showReportLimitPopup();
    }

    private void showReportLimitPopup() {
        Util.buildDialog(this, getString(R.string.popup_report_limit_error_title), getString(R.string.popup_report_limit_error_text), Constants.ERROR_LIMIT).show();
    }

    @Override
    public void isAllowedToAdd() {
        showAddShopDialog();
    }

    @Override
    public void isNotAllowedToAdd() {
        showAddLimitAlertPopup();

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
