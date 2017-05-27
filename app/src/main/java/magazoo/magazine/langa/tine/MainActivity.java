package magazoo.magazine.langa.tine;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import magazoo.magazine.langa.tine.model.StoreMarker;

import static magazoo.magazine.langa.tine.R.id.map;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static final int MY_LOCATION_REQUEST_CODE = 523;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final int ACCURACY_DESIRED = 8;
    private static final int ZOOM_LEVEL_DESIRED = 18;

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference mStoreRef;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private float mCurrentAccuracy = 0;
    private LatLng mCurrentLocation;

    private LatLngBounds bounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createLocationRequest();

        mStoreRef = FirebaseDatabase.getInstance().getReference("Stores");

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAccuracy != 0 && mCurrentAccuracy <= ACCURACY_DESIRED) {
                    if (auth.getCurrentUser() != null) {
                        showAddShopDialog(mCurrentLocation);
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                } else {
                    buildErrorDialog(getResources().getString(R.string.popup_accuracy_error_title), getResources().getString(R.string.popup_accuracy_error_text)).show();
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);

        mapFragment.getMapAsync(this);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
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
                .customView(layout, true)
                .cancelable(false);
    }

    private MaterialDialog.Builder buildErrorDialog(String title, String content) {

        return new MaterialDialog.Builder(this)
                .title(title)
                .content(content)
                .positiveText(R.string.agree);
    }

    private void onNewMarkerAdded() {

        mStoreRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                StoreMarker marker = dataSnapshot.getValue(StoreMarker.class);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(marker.getLat(), marker.getLon()))
                        .title(marker.getName()));
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

                ArrayList<StoreMarker> filteredMarkers = new ArrayList<>();

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    StoreMarker marker = markerSnapshot.getValue(StoreMarker.class);
                    if (bounds.contains(new LatLng(marker.getLat(), marker.getLon()))) {
                        filteredMarkers.add(marker);
                    }
                }

                for (int i = 0; i < filteredMarkers.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(filteredMarkers.get(i).getLat(), filteredMarkers.get(i).getLon()))
                            .title(filteredMarkers.get(i).getName()));
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Not logged in", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void addMarkerToFirebase(StoreMarker markerToAdd) {
        StoreMarker marker = new StoreMarker(markerToAdd.getName(), markerToAdd.getLat(),
                markerToAdd.getLon(), markerToAdd.getType(), markerToAdd.getPos(), markerToAdd.getNonStop(),
                markerToAdd.getDescription(), 0.00,
                auth.getCurrentUser().getUid());

        mStoreRef.push().setValue(marker);
    }

    @Override
    protected void onStart() {
        auth = FirebaseAuth.getInstance();
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
        auth.signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        Marker lastOpenned = null;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        setMapTheme();
        getMapBounds();
        setMyLocationEnabled();
        setOnCameraChangeListener();
        onNewMarkerAdded();
        displayFirebaseMarkers();
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
                //mMap.clear();
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                mMap.clear();
                getMapBounds();
                displayFirebaseMarkers();
            }
        });
    }

    private void getMapBounds() {

        bounds = MainActivity.this.mMap
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocation, ZOOM_LEVEL_DESIRED));
        } else {
            if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                zoomToCurrentLocation();
            }

        }

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
                requestLocationPermissions();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        } else {
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
        final MaterialDialog dialog = buildDialog(getResources().getString(R.string.popup_add_shop_title), R.layout.add_shop).show();

        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner_type);
        final CheckBox chkPos = (CheckBox) dialog.findViewById(R.id.check_pos);
        final CheckBox chkNonstop = (CheckBox) dialog.findViewById(R.id.check_nonstop);

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
        categories.add(getResources().getString(R.string.popup_add_shop_type));
        categories.add(getResources().getString(R.string.popup_add_shop_type_small));
        categories.add(getResources().getString(R.string.popup_add_shop_farmer));
        categories.add(getResources().getString(R.string.popup_add_shop_supermarket));
        categories.add(getResources().getString(R.string.popup_add_shop_type_hypermarket));

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
                if(!spinner.getSelectedItem().equals(getString(R.string.popup_add_shop_type))){
                    addMarkerToFirebase(new StoreMarker("name", latlng.latitude, latlng.longitude,
                            spinner.getSelectedItem().toString(), chkPos.isChecked(),
                            chkNonstop.isChecked(), "test description", 0.00, ""));
                    dialog.dismiss();
                }else{
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.popup_add_shop_error), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentAccuracy = location.getAccuracy();
        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//        Toast.makeText(this, "Accuracy: " + location.getAccuracy(), Toast.LENGTH_SHORT).show();
    }
}
