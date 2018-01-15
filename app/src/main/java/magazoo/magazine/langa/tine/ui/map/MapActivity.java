package magazoo.magazine.langa.tine.ui.map;

import android.Manifest.permission;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

import magazoo.magazine.langa.tine.ui.login.LoginView;
import magazoo.magazine.langa.tine.ui.profile.ProfileActivity;
import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.constants.IConstants;
import magazoo.magazine.langa.tine.model.Marker;
import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.ui.tutorial.TutorialActivity;
import magazoo.magazine.langa.tine.utils.OnErrorHandledListener;
import magazoo.magazine.langa.tine.utils.Util;

import static magazoo.magazine.langa.tine.R.id.map;

public class MapActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener, IConstants, OnErrorHandledListener {

    private static final String TAG = MapActivity.class.getSimpleName();

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
    private MapActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Utils.init(mContext);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        onboardingNeeded();
        checkInternetConnection();
        checkGPSConnection();
        initializeDatabaseReference();
        initUI();
        setupNavigationDrawer();
        setUpMap();
        setupApiClientLocation();
        createLocationRequest();
    }

    private void onboardingNeeded() {

       if(isFirstRun()){
           startTutorial();
       }
    }

    private void startTutorial() {
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

    private void initializeDatabaseReference() {
        mStoreRef = FirebaseDatabase.getInstance().getReference("Stores");
        mReportRef = FirebaseDatabase.getInstance().getReference("Reports");
    }

    private void checkInternetConnection() {
        if (!NetworkUtils.isConnected()) {
            buildNoInternetErrorDialog();
        }
    }

    private void checkGPSConnection() {
        if (!Util.isGPSAvailable()) {
            buildNoGPSErrorDialog();
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
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (mAuth.getCurrentUser() != null) {
            navigationView.getMenu().findItem(R.id.nav_signout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
            View headerLayout = navigationView.getHeaderView(0);
            TextView headerText = headerLayout.findViewById(R.id.signedInUserEmail);
            headerText.setText(mAuth.getCurrentUser().getEmail());
        } else {
            navigationView.getMenu().findItem(R.id.nav_signin).setVisible(true);
        }
    }

    private void initUI() {

        //mToolbar initialization
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //floating button for adding shops
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Util.isInternetAvailable(mContext)) {

                    if (mCurrentAccuracy != 0 && mCurrentAccuracy <= ACCURACY_DESIRED) {
                        if (mAuth.getCurrentUser() != null) {
                            checkIfAllowedToAdd();
                        } else {
                            startActivity(new Intent(MapActivity.this, LoginView.class));
                            finish();
                        }
                    } else {
                        Util.buildDialog(mContext, getString(R.string.popup_accuracy_error_title), getString(R.string.popup_accuracy_error_text) + "\n" + getString(R.string.popup_current_accuracy) + " " + mCurrentAccuracy, ERROR_ACCURACY).show();
                    }

                } else {
                    buildNoInternetErrorDialog();
                }
            }
        });

        // cardview for shop details
        initUIShopDetails();

    }

    private void buildNoInternetErrorDialog() {
        Util.buildDialog(mContext, getString(R.string.popup_connection_error_title), getString(R.string.popup_connection_error_text), ERROR_INTERNET).show();
    }

    private void buildNoGPSErrorDialog() {
        Util.buildDialog(mContext, getString(R.string.popup_gps_error_title), getString(R.string.popup_gps_error_text), ERROR_LOCATION).show();
    }


    private void checkIfAllowedToAdd() {
        getShopsAddedToday(new OnGetShopsFromDatabaseListener() {
            @Override
            public void onDataFetched(ArrayList<Marker> shopsAddedToday) {
                if (shopsAddedToday.size() <= ADD_SHOP_LIMIT) {
                    showAddShopDialog(mCurrentLocation);
                } else {
                    Util.buildDialog(mContext, getString(R.string.popup_shop_limit_error_title), getString(R.string.popup_shop_limit_error_text), ERROR_LIMIT).show();
                }
            }
        });
    }

    private void getShopsAddedToday(final OnGetShopsFromDatabaseListener listener) {

        final ArrayList<Marker> addedShopsToday = new ArrayList<>();
        //filter data based on logged in user
        Query query = mStoreRef.orderByChild("createdBy").equalTo(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    Marker store = markerSnapshot.getValue(Marker.class);
                    Date createdAt = new Date(store.getCreatedAt());
                    long now = new Date().getTime();
                    Date nowDate = new Date(now);

                    if (Util.isSameDay(createdAt, nowDate)) {
                        addedShopsToday.add(store);
                    }
                }

                listener.onDataFetched(addedShopsToday);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkIfAllowedToReport() {

        getReportsAddedToday(new OnGetReportsFromDatabaseListener() {
            @Override
            public void onDataFetched(ArrayList<Report> reportsAddedToday) {
                if (reportsAddedToday.size() <= REPORT_SHOP_LIMIT) {
                    showReportPopup();
                } else {
                    Util.buildDialog(mContext, getString(R.string.popup_report_limit_error_title), getString(R.string.popup_report_limit_error_text), ERROR_LIMIT).show();
                }
            }
        });
    }

    private void getReportsAddedToday(final OnGetReportsFromDatabaseListener listener) {

        final ArrayList<Report> reportsToday = new ArrayList<>();
        //filter data based on logged in user
        Query query = mReportRef.orderByChild("reportedBy").equalTo(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    Report report = markerSnapshot.getValue(Report.class);
                    Date reportedAt = new Date(report.getReportedAt());
                    long now = new Date().getTime();
                    Date nowDate = new Date(now);

                    if (Util.isSameDay(reportedAt, nowDate)) {
                        reportsToday.add(report);
                    }
                }

                listener.onDataFetched(reportsToday);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                if (mAuth.getCurrentUser() != null) {
                    checkIfAllowedToReport();
                } else {
                    startActivity(new Intent(MapActivity.this, LoginView.class));
                    finish();
                }
            }
        });
    }

    private void showReportPopup() {

        final MaterialDialog dialog = buildCustomDialog(getString(R.string.popup_report_shop_title), R.layout.report_shop).show();
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

                mCurrentReportedShop = new Report(mCurrentOpenShop.getId(), REPORT_LOCATION, false, mAuth.getCurrentUser().getUid(), new Date().getTime());
                checkIfDuplicateLocationReport();
                closeDialog(dialog);
            }

            private void checkIfDuplicateLocationReport() {
                final ArrayList<Report> locationReports = new ArrayList<>();

                Query query = mReportRef.orderByChild("reportedBy").equalTo(mAuth.getCurrentUser().getUid());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                            Report report = markerSnapshot.getValue(Report.class);
                            if (report.getRegards().equals("location")) {
                                locationReports.add(report);
                            }
                        }

                        if (!locationReports.contains(mCurrentReportedShop)) {
                            writeReportToDatabase(new OnReportWrittenToDatabaseListener() {
                                @Override
                                public void onReportWritten() {
                                    showReportThanksPopup();
                                }
                            }, mCurrentOpenShop, REPORT_LOCATION, false);
                        } else {
                            Util.buildDialog(mContext, getString(R.string.popup_location_report_duplicate_error_title), getString(R.string.popup_location_report_duplicate_error_text), ERROR_LIMIT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        });

        report_247.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                mCurrentReportedShop = new Report(mCurrentOpenShop.getId(), REPORT_247, !mCurrentOpenShop.getNonstop(), mAuth.getCurrentUser().getUid(), new Date().getTime());
                checkIfDuplicate247Report();
                closeDialog(dialog);
            }

            private void checkIfDuplicate247Report() {
                final ArrayList<Report> nonStopReports = new ArrayList<>();

                Query query = mReportRef.orderByChild("reportedBy").equalTo(mAuth.getCurrentUser().getUid());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                            Report report = markerSnapshot.getValue(Report.class);
                            if (report.getRegards().equals("nonstop")) {
                                nonStopReports.add(report);
                            }
                        }

                        if (!nonStopReports.contains(mCurrentReportedShop)) {
                            writeReportToDatabase(new OnReportWrittenToDatabaseListener() {
                                @Override
                                public void onReportWritten() {
                                    showReportThanksPopup();
                                }
                            }, mCurrentOpenShop, REPORT_247, !mCurrentOpenShop.getNonstop());
                        } else {
                            Util.buildDialog(mContext, getString(R.string.popup_nonstop_report_duplicate_error_title), getString(R.string.popup_nonstop_report_duplicate_error_text), ERROR_LIMIT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }


        });

        report_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentReportedShop = new Report(mCurrentOpenShop.getId(), REPORT_POS, !mCurrentOpenShop.getPos(), mAuth.getCurrentUser().getUid(), new Date().getTime());
                checkIfDuplicatePosReport();
                closeDialog(dialog);
            }

            private void checkIfDuplicatePosReport() {
                final ArrayList<Report> posReports = new ArrayList<>();

                Query query = mReportRef.orderByChild("reportedBy").equalTo(mAuth.getCurrentUser().getUid());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                            Report report = markerSnapshot.getValue(Report.class);
                            if (report.getRegards().equals("pos")) {
                                posReports.add(report);
                            }
                        }

                        if (!posReports.contains(mCurrentReportedShop)) {
                            writeReportToDatabase(new OnReportWrittenToDatabaseListener() {
                                @Override
                                public void onReportWritten() {
                                    showReportThanksPopup();
                                }
                            }, mCurrentOpenShop, REPORT_POS, !mCurrentOpenShop.getPos());
                        } else {
                            Util.buildDialog(mContext, getString(R.string.popup_pos_report_duplicate_error_title), getString(R.string.popup_pos_report_duplicate_error_text), ERROR_LIMIT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        });

        report_tickets.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentReportedShop = new Report(mCurrentOpenShop.getId(), REPORT_TICKETS, !mCurrentOpenShop.getTickets(), mAuth.getCurrentUser().getUid(), new Date().getTime());
                checkIfDuplicateTicketsReport();
                closeDialog(dialog);
            }

            private void checkIfDuplicateTicketsReport() {
                final ArrayList<Report> ticketsReports = new ArrayList<>();

                Query query = mReportRef.orderByChild("reportedBy").equalTo(mAuth.getCurrentUser().getUid());

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                            Report report = markerSnapshot.getValue(Report.class);
                            if (report.getRegards().equals("tickets")) {
                                ticketsReports.add(report);
                            }
                        }

                        if (!ticketsReports.contains(mCurrentReportedShop)) {
                            writeReportToDatabase(new OnReportWrittenToDatabaseListener() {
                                @Override
                                public void onReportWritten() {
                                    showReportThanksPopup();                                }
                            }, mCurrentOpenShop, REPORT_TICKETS, !mCurrentOpenShop.getTickets());
                        } else {
                            Util.buildDialog(mContext, getString(R.string.popup_tickets_report_duplicate_error_title), getString(R.string.popup_tickets_report_duplicate_error_text), ERROR_LIMIT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void showReportThanksPopup() {
        Util.buildDialog(mContext, getString(R.string.thanks_report), getString(R.string.details_report), 0).show();
    }

    private void closeDialog(MaterialDialog dialog) {
        //TODO: need to find a way to update the cardview without closing it
        mShopDetails.setVisibility(View.GONE);
        dialog.dismiss();
    }

    private void writeReportToDatabase(final OnReportWrittenToDatabaseListener listener, final Marker shop, final String reportTarget, final boolean howisit) {
        mReportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    Report reportedShop = new Report(shop.getId(), reportTarget, howisit, mAuth.getCurrentUser().getUid(), new Date().getTime());
                    mReportRef.push().setValue(reportedShop);
                    listener.onReportWritten();
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

    private MaterialDialog.Builder buildCustomDialog(String title, int layout) {

        return new MaterialDialog.Builder(this)
                .title(title)
                .customView(layout, true);
    }

    private void onNewMarkerAdded() {

        mStoreRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Marker marker = dataSnapshot.getValue(Marker.class);
                assert marker != null;
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

                mMarkersInBounds = new ArrayList<>();

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    Marker marker = markerSnapshot.getValue(Marker.class);
                    //update model with id from firebase
                    marker.setId(markerSnapshot.getKey());
                    if (mBounds.contains(new LatLng(marker.getLat(), marker.getLon()))) {
                        mMarkersInBounds.add(marker);
                    }
                }

                mMap.clear();

                for (int i = 0; i < mMarkersInBounds.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mMarkersInBounds.get(i).getLat(), mMarkersInBounds.get(i).getLon()))
                            .title(mMarkersInBounds.get(i).getId()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapActivity.this, "Not logged in", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void addMarkerToFirebase(Marker markerToAdd) {
        mStoreRef.push().setValue(markerToAdd).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    showAddThanksPopup();
                }else{
                    Toast.makeText(mContext, "A network error occured. Pleaase try again later", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void showAddThanksPopup() {
        Util.buildDialog(mContext, getString(R.string.thanks_adding), getString(R.string.details_adding), 0).show();
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
            startActivity(new Intent(MapActivity.this, ProfileActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_signout) {
            signOut();
        } else if (id == R.id.nav_signin) {
            startActivity(new Intent(MapActivity.this, LoginView.class));
            finish();
        } else if(id == R.id.nav_tutorial) {
            startTutorial();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        startActivity(new Intent(MapActivity.this, LoginView.class));
        finish();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

                mMap.animateCamera((CameraUpdateFactory.newLatLngZoom(marker.getPosition(), ZOOM_LEVEL_DESIRED)));
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
        if (mCurrentZoomLevel > 1 && mCurrentZoomLevel >= ZOOM_LEVEL_DESIRED) {
            displayFirebaseMarkers();
        }

    }

    private void showShopDetails(Marker d) {

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
                        Util.buildDialog(mContext, "Max zoom reached", "Max zoom", ERROR_MAX_ZOOM).show();
                    }
                }
            }
        });
    }

    private void setZoomLevel() {
        mCurrentZoomLevel = mMap.getCameraPosition().zoom;
    }

    private void getMapBounds() {
        mBounds = MapActivity.this.mMap
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, ZOOM_LEVEL_DESIRED));
    }

    public boolean requestLocationPermissions() {
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
                Util.buildDialog(mContext, getString(R.string.popup_location_permission_error_title), getString(R.string.popup_location_permission_error_text), ERROR_PERMISSION).show();
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

        final MaterialDialog dialog = buildCustomDialog(getString(R.string.popup_add_shop_title), R.layout.add_shop).show();
        final MaterialSpinner spinner = (MaterialSpinner) dialog.findViewById(R.id.spinner_type);
        final CheckBox chkPos = (CheckBox) dialog.findViewById(R.id.checkPos);
        final CheckBox chkNonstop = (CheckBox) dialog.findViewById(R.id.checkNonstop);
        final CheckBox chkTickets = (CheckBox) dialog.findViewById(R.id.checkTickets);
        final EditText editDescription = (EditText) dialog.findViewById(R.id.editDescription);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MapActivity.this, "selected", Toast.LENGTH_SHORT).show();
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
                    addMarkerToFirebase(new Marker(ID_PLACEHOLDER, "name", latlng.latitude, latlng.longitude,
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
