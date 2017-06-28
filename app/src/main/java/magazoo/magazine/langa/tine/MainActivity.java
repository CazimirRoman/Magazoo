package magazoo.magazine.langa.tine;

import android.Manifest.permission;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LocationUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;
import magazoo.magazine.langa.tine.model.StoreMarker;
import magazoo.magazine.langa.tine.model.StoreReport;

import static magazoo.magazine.langa.tine.R.id.map;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ID_PLACEHOLDER = "check model property for id";
    private static final int MY_LOCATION_REQUEST_CODE = 523;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final int ACCURACY_DESIRED = 8;
    private static final int ZOOM_LEVEL_DESIRED = 15;
    private static final int ADD_SHOP_LIMIT = 5;
    private static final int REPORT_SHOP_LIMIT = 3;
    private static final int ERROR_ACCURACY = 567;
    private static final int ERROR_INTERNET = 876;
    private static final int ERROR_LOCATION = 159;
    private static final int ERROR_PERMISSION = 670;
    private static final int ERROR_MAX_ZOOM = 109;
    private static final int ERROR_LIMIT = 643;
    private static final String REPORT_LOCATION = "location";
    private static final String REPORT_POS = "pos";
    private static final String REPORT_247 = "nonstop";
    private static final String REPORT_TICKETS = "tickets";

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mStoreRef;
    private DatabaseReference mReportRef;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private float mCurrentAccuracy = 0;
    private LatLng mCurrentLocation;
    private LatLng mCurrentOpenShopLatLng;
    private StoreMarker mCurrentOpenShop;
    private float mCurrentZoomLevel;
    private LatLngBounds mBounds;
    private ArrayList<StoreMarker> mFilteredMarkers;
    private CardView mShopDetails;
    private TextView mShopTypeLabel;
    private TextView mNonStopLabel;
    private TextView mPosLabel;
    private TextView mTicketsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(this);
        setContentView(R.layout.activity_main);
        checkInternetConnection();
        checkGPSConnection();
        initializeDatabaseReference();
        initUI();
        setupNavigationView();
        setUpMap();
        setupApiClientLocation();
        createLocationRequest();
    }

    private void initializeDatabaseReference() {
        mStoreRef = FirebaseDatabase.getInstance().getReference("Stores");
        mReportRef = FirebaseDatabase.getInstance().getReference("Reports");
    }

    private void checkGPSConnection() {
        if (!LocationUtils.isLocationEnabled()) {
            buildErrorDialog(getString(R.string.popup_gps_error_title), getString(R.string.popup_gps_error_text), ERROR_LOCATION).show();
        }
    }

    private void checkInternetConnection() {
        if (!NetworkUtils.isConnected()) {
            buildErrorDialog(getString(R.string.popup_connection_error_title), getString(R.string.popup_connection_error_text), ERROR_INTERNET).show();
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

    private void setupNavigationView() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initUI() {

        //mToolbar initialization
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //floating button for adding shops
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mCurrentAccuracy != 0 && mCurrentAccuracy <= ACCURACY_DESIRED

                if (true) {
                    if (mAuth.getCurrentUser() != null) {
                        checkIfAllowedToAdd();
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                } else {
                    buildErrorDialog(getString(R.string.popup_accuracy_error_title), getString(R.string.popup_accuracy_error_text) + "\n" + getString(R.string.popup_current_accuracy) + " " + mCurrentAccuracy, ERROR_ACCURACY).show();
                }
            }
        });

        // cardview for shop details
        initUIShopDetails();

    }

    private void checkIfAllowedToAdd() {

        final ArrayList<StoreMarker> addedShopsToday = new ArrayList<>();
        //filter data based on logged in user
        Query query = mStoreRef.orderByChild("createdBy").equalTo(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    StoreMarker store = markerSnapshot.getValue(StoreMarker.class);
                    Date createdAt = new Date(store.getCreatedAt());
                    long now = new Date().getTime();
                    Date nowDate = new Date(now);

                    if (isSameDay(createdAt, nowDate)) {
                        addedShopsToday.add(store);
                    }
                }

                if (addedShopsToday.size() <= ADD_SHOP_LIMIT) {
                    showAddShopDialog(mCurrentLocation);
                } else {
                    buildErrorDialog(getString(R.string.popup_shop_limit_error_title), getString(R.string.popup_shop_limit_error_text), ERROR_LIMIT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkIfAllowedToReport() {

        final ArrayList<StoreReport> reportsToday = new ArrayList<>();
        //filter data based on logged in user
        Query query = mReportRef.orderByChild("reportedBy").equalTo(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    StoreReport report = markerSnapshot.getValue(StoreReport.class);
                    Date reportedAt = new Date(report.getReportedAt());
                    long now = new Date().getTime();
                    Date nowDate = new Date(now);

                    if (isSameDay(reportedAt, nowDate)) {
                        reportsToday.add(report);
                    }
                }

                if (reportsToday.size() <= REPORT_SHOP_LIMIT) {
                    showReportPopup();
                } else {
                    buildErrorDialog(getString(R.string.popup_report_limit_error_title), getString(R.string.popup_report_limit_error_text), ERROR_LIMIT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean isSameDay(Date day1, Date day2) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(day1);
        cal2.setTime(day2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return sameDay;
    }

    private void initUIShopDetails() {
        mShopDetails = (CardView) findViewById(R.id.shop_details);
        mShopTypeLabel = (TextView) mShopDetails.findViewById(R.id.shop_type_label);
        mNonStopLabel = (TextView) mShopDetails.findViewById(R.id.nonstop_label);
        mPosLabel = (TextView) mShopDetails.findViewById(R.id.pos_label);
        mTicketsLabel = (TextView) mShopDetails.findViewById(R.id.tickets_label);

        ImageButton buttonNavigate = (ImageButton) mShopDetails.findViewById(R.id.button_navigate);
        ImageButton buttonReport = (ImageButton) mShopDetails.findViewById(R.id.button_report);

        buttonNavigate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToShop();
            }
        });

        buttonReport.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() != null) {
                    checkIfAllowedToReport();
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
    }

    private void showReportPopup() {

        final MaterialDialog dialog = buildDialog(getString(R.string.popup_report_shop_title), R.layout.report_shop).show();
        Button report_location = (Button) dialog.findViewById(R.id.button_report_location);
        Button report_247 = (Button) dialog.findViewById(R.id.button_report_247);
        Button report_pos = (Button) dialog.findViewById(R.id.button_report_pos);
        Button report_tickets = (Button) dialog.findViewById(R.id.button_report_tickets);

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
                writeReportToDatabase(mCurrentOpenShop, REPORT_LOCATION, false);
                closeDialog(dialog);
            }
        });

        report_247.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                writeReportToDatabase(mCurrentOpenShop, REPORT_247, !mCurrentOpenShop.getNonstop());
                closeDialog(dialog);
            }
        });

        report_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                writeReportToDatabase(mCurrentOpenShop, REPORT_POS, !mCurrentOpenShop.getPos());
                closeDialog(dialog);
            }
        });

        report_tickets.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                writeReportToDatabase(mCurrentOpenShop, REPORT_TICKETS, !mCurrentOpenShop.getTickets());
                closeDialog(dialog);
            }
        });

    }

    private void closeDialog(MaterialDialog dialog) {
        dialog.dismiss();
    }

    private void writeReportToDatabase(final StoreMarker shop, final String reportTarget, final boolean howisit) {
        mReportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    StoreReport reportedShop = new StoreReport(shop.getId(), reportTarget, howisit, mAuth.getCurrentUser().getUid(), new Date().getTime());
                    mReportRef.push().setValue(reportedShop);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private MaterialDialog.Builder buildDialog(String title, int layout) {

        return new MaterialDialog.Builder(this)
                .title(title)
                .customView(layout, true);
    }

    private MaterialDialog.Builder buildErrorDialog(String title, String content, final int errorType) {

        return new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .positiveText(R.string.agree)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (errorType) {
                            case ERROR_ACCURACY:
                                dialog.dismiss();
                                break;
                            case ERROR_PERMISSION:
                                requestLocationPermissions();
                                break;
                            case ERROR_MAX_ZOOM:
                                dialog.dismiss();
                                zoomToCurrentLocation();
                                break;
                            default:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
    }

    private void onNewMarkerAdded() {

        mStoreRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                StoreMarker marker = dataSnapshot.getValue(StoreMarker.class);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(marker.getLat(), marker.getLon()))
                        .title(s));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayFirebaseMarkers() {

        mStoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mFilteredMarkers = new ArrayList<>();

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    StoreMarker marker = markerSnapshot.getValue(StoreMarker.class);
                    //update model with id from firebase
                    marker.setId(markerSnapshot.getKey());
                    if (mBounds.contains(new LatLng(marker.getLat(), marker.getLon()))) {
                        mFilteredMarkers.add(marker);
                    }
                }

                mMap.clear();

                for (int i = 0; i < mFilteredMarkers.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mFilteredMarkers.get(i).getLat(), mFilteredMarkers.get(i).getLon()))
                            .title(mFilteredMarkers.get(i).getId()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Not logged in", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void addMarkerToFirebase(StoreMarker markerToAdd) {
        mStoreRef.push().setValue(markerToAdd);
    }

    @Override
    protected void onStart() {
        mAuth = FirebaseAuth.getInstance();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_signout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {

                mMap.animateCamera((CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM_LEVEL_DESIRED)));
                if (mShopDetails.getVisibility() == View.GONE) {
                    for (StoreMarker d : mFilteredMarkers) {
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
        if (mCurrentZoomLevel > 1 && mCurrentZoomLevel >= ZOOM_LEVEL_DESIRED) {
            displayFirebaseMarkers();
        }

    }

    private void showShopDetails(StoreMarker d) {

        mCurrentOpenShop = d;
        mCurrentOpenShopLatLng = new LatLng(d.getLat(), d.getLon());
        mShopTypeLabel.setText(d.getType());

        if (d.getNonstop()) {
            mNonStopLabel.setVisibility(View.VISIBLE);
        } else {
            mNonStopLabel.setVisibility(View.GONE);
        }

        if (d.getPos()) {
            mPosLabel.setVisibility(View.VISIBLE);
        } else {
            mPosLabel.setVisibility(View.GONE);
        }

        if (d.getTickets()) {
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
                if (mCurrentZoomLevel > 1 && mCurrentZoomLevel >= ZOOM_LEVEL_DESIRED) {
                    mMap.clear();
                    getMapBounds();
                    displayFirebaseMarkers();
                    onNewMarkerAdded();
                } else {
                    //first run only
                    if (mCurrentZoomLevel != 2) {
                        buildErrorDialog("Max zoom reached", "Max zoom", ERROR_MAX_ZOOM).show();
                    }
                }
            }
        });
    }

    private void setZoomLevel() {
        mCurrentZoomLevel = mMap.getCameraPosition().zoom;
    }

    private void getMapBounds() {
        mBounds = MainActivity.this.mMap
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

    private void zoomToCurrentLocation() {
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, ZOOM_LEVEL_DESIRED));
    }

    private boolean requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setMyLocationEnabled();
            } else {
                buildErrorDialog(getString(R.string.popup_location_permission_error_title), getString(R.string.popup_location_permission_error_text), ERROR_PERMISSION).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Toast.makeText(this, "Location data: " + mLastLocation.getLatitude() + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                LatLng marker = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, ZOOM_LEVEL_DESIRED));
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

    private void showAddShopDialog(final LatLng latlng) {
        final MaterialDialog dialog = buildDialog(getString(R.string.popup_add_shop_title), R.layout.add_shop).show();

        final MaterialSpinner spinner = (MaterialSpinner) dialog.findViewById(R.id.spinner_type);
        final CheckBox chkPos = (CheckBox) dialog.findViewById(R.id.checkPos);
        final CheckBox chkNonstop = (CheckBox) dialog.findViewById(R.id.checkNonstop);
        final CheckBox chkTickets = (CheckBox) dialog.findViewById(R.id.checkTickets);
        final EditText editDescription = (EditText) dialog.findViewById(R.id.editDescription);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, "selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        List<String> categories = new ArrayList<String>();
        categories.add(getString(R.string.popup_add_shop_type_small));
        categories.add(getString(R.string.popup_add_shop_farmer));
        categories.add(getString(R.string.popup_add_shop_supermarket));
        categories.add(getString(R.string.popup_add_shop_type_hypermarket));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        Button buttonAdd = (Button) dialog.findViewById(R.id.buttonAdd);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);

        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        buttonAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!spinner.getSelectedItem().equals(getString(R.string.popup_add_shop_type))) {
                    addMarkerToFirebase(new StoreMarker(ID_PLACEHOLDER, "name", latlng.latitude, latlng.longitude,
                            spinner.getSelectedItem().toString(), chkPos.isChecked(),
                            chkNonstop.isChecked(), chkTickets.isChecked(), editDescription.getText().toString(), 0.00, mAuth.getCurrentUser().getUid()));
                    dialog.dismiss();
                } else {
                    spinner.setError(getString(R.string.popup_add_shop_type));
                }

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentAccuracy = location.getAccuracy();
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }
}
