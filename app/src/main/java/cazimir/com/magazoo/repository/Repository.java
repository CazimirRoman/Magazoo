package cazimir.com.magazoo.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TimingLogger;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cazimir.com.magazoo.constants.Constants;
import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.map.OnAddMarkerToDatabaseCallback;
import cazimir.com.magazoo.presenter.map.OnDeleteShopCallback;
import cazimir.com.magazoo.presenter.map.OnDuplicateReportCallback;
import cazimir.com.magazoo.presenter.map.OnGetMarkersListener;
import cazimir.com.magazoo.presenter.map.OnGetShopsAddedTodayListener;
import cazimir.com.magazoo.reports.OnGetAllShopsReportCallback;
import cazimir.com.magazoo.ui.map.OnGetReportsFromDatabaseListener;
import cazimir.com.magazoo.ui.map.OnReportWrittenToDatabaseCallback;
import cazimir.com.magazoo.utils.ApiFailedException;
import cazimir.com.magazoo.utils.PlacesService;
import cazimir.com.magazoo.utils.Util;

import static cazimir.com.magazoo.constants.Constants.SMALL_SHOP;

/**
 * TODO: Add a class header comment!
 */
public class Repository implements IRepository {

    private static final String TAG = Repository.class.getSimpleName();

    private volatile boolean mPaused = false;

    private DatabaseReference mStoreRef = FirebaseDatabase.getInstance().getReference("Stores");
    private DatabaseReference mStoreRefDev = FirebaseDatabase.getInstance().getReference("StoresDev");
    private DatabaseReference mReportRef = FirebaseDatabase.getInstance().getReference("Reports");

    public void getReportsAddedToday(final OnGetReportsFromDatabaseListener mapPresenter, String userId) {

        final ArrayList<Report> reportsToday = new ArrayList<>();
        //filter data based on logged in user
        Query query = mReportRef.orderByChild("reportedBy").equalTo(userId);

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

                mapPresenter.onDataFetched(reportsToday);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void getMarkers(final OnGetMarkersListener mapPresenter, final LatLngBounds bounds) {
        Log.d(TAG, "getMarkers: called");

                mStoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList<Shop> markersToShow = new ArrayList<>();
                                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                                    Shop marker = markerSnapshot.getValue(Shop.class);
                                    //update model with id from firebase
                                    if (marker != null) {
                                        marker.setId(markerSnapshot.getKey());
                                        markersToShow.add(marker);
                                    }
                                }

                                mapPresenter.onGetAllMarkersSuccess(markersToShow);
                            }
                        }).start();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        mapPresenter.onGetAllMarkersFailed(databaseError.getMessage());
                    }
                });
    }

    @Override
    public void addMarkerToDatabase(final OnAddMarkerToDatabaseCallback mapPresenter, Shop shop) {

        String shopId = mStoreRef.push().getKey();
        shop.setId(shopId);

        mStoreRef.child(shopId).setValue(shop).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mapPresenter.onSuccess();
                } else {
                    mapPresenter.onFailed(task.getException().getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mapPresenter.onFailed(e.getMessage());
            }
        });
    }

    @Override
    public void deleteShop(final OnDeleteShopCallback mapPresenter, String id) {
        mStoreRef.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "deleteShop: success!");
                    mapPresenter.onSuccess();
                }else{
                    mapPresenter.onFailed(task.getException().toString());
                }
            }
        });
    }

    @Override
    public void getAllShopsForReport(final OnGetAllShopsReportCallback callback) {
        mStoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int totalNumberOfShopsWorld = 0;
                int totalNumberOfShopsBucuresti = 0;

                int smallShopsWorld = 0;
                int smallShopsBucuresti = 0;
                int farmersMarketsWorld = 0;
                int farmersMarketsBucuresti = 0;
                int gasStationsWorld = 0;
                int gasStationsBucuresti = 0;
                int superMarketsWorld = 0;
                int superMarketsBucuresti = 0;
                int shoppingCentersWorld = 0;
                int shoppingCentersBucuresti = 0;

                int sector1 = 0;
                int sector2 = 0;
                int sector3 = 0;
                int sector4 = 0;
                int sector5 = 0;
                int sector6 = 0;


                Map<String, Integer> shopCountryWorld = new HashMap<>();
                Map<String, Integer> shopTypeWorld = new HashMap<>();
                Map<String, Integer> shopTypeBucuresti = new HashMap<>();
                Map<String, Integer> shopSectorBucuresti = new HashMap<>();

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    Shop marker = markerSnapshot.getValue(Shop.class);

                    if(marker.getCity() != null){
                        if(marker.getCity().equals("București")){

                            totalNumberOfShopsBucuresti++;

                            switch(marker.getAdminName()){
                                case "Sector 1":
                                    sector1++;
                                    break;
                                case "Sector 2":
                                    sector2++;
                                    break;
                                case "Sector 3":
                                    sector3++;
                                    break;
                                case "Sector 4":
                                    sector4++;
                                    break;
                                case "Sector 5":
                                    sector5++;
                                    break;

                                case "Sector 6":
                                    sector6++;
                                    break;
                            }

                            switch(marker.getType()){
                                case SMALL_SHOP:
                                    smallShopsBucuresti++;
                                    break;
                                case Constants.FARMER_MARKET:
                                    farmersMarketsBucuresti++;
                                    break;
                                case Constants.GAS_STATION:
                                    gasStationsBucuresti++;
                                    break;
                                case Constants.SUPERMARKET:
                                    superMarketsBucuresti++;
                                    break;
                                case Constants.SHOPPING_CENTER:
                                    shoppingCentersBucuresti++;
                                    break;
                            }
                        }
                    }

                    switch(marker.getType()){
                        case SMALL_SHOP:
                            smallShopsWorld++;
                            break;
                        case Constants.FARMER_MARKET:
                            farmersMarketsWorld++;
                            break;
                        case Constants.GAS_STATION:
                            gasStationsWorld++;
                            break;
                        case Constants.SUPERMARKET:
                            superMarketsWorld++;
                            break;
                        case Constants.SHOPPING_CENTER:
                            shoppingCentersWorld++;
                            break;
                    }

                    String country = marker.getCountry();
                    String type = marker.getType();

                    if(country != null){

                        //shop country exists in hashmap
                        if(shopCountryWorld.get(country) != null){
                            shopCountryWorld.put(country, shopCountryWorld.get(country) + 1);
                        }else{
                            shopCountryWorld.put(country, 1);
                        }
                    }

                    if(type != null){

                        //shop type exists in hashmap
                        if(shopTypeWorld.get(type) != null){
                            shopTypeWorld.put(type, shopTypeWorld.get(type) + 1);
                        }else{
                            shopTypeWorld.put(type, 1);
                        }
                    }

                    shopTypeBucuresti.put(Constants.SMALL_SHOP, smallShopsBucuresti);
                    shopTypeBucuresti.put(Constants.FARMER_MARKET, farmersMarketsBucuresti);
                    shopTypeBucuresti.put(Constants.GAS_STATION, gasStationsBucuresti);
                    shopTypeBucuresti.put(Constants.SUPERMARKET, superMarketsBucuresti);
                    shopTypeBucuresti.put(Constants.SHOPPING_CENTER, shoppingCentersBucuresti);

                    shopSectorBucuresti.put("Sector 1", sector1);
                    shopSectorBucuresti.put("Sector 2", sector2);
                    shopSectorBucuresti.put("Sector 3", sector3);
                    shopSectorBucuresti.put("Sector 4", sector4);
                    shopSectorBucuresti.put("Sector 5", sector5);
                    shopSectorBucuresti.put("Sector 6", sector6);

                    totalNumberOfShopsWorld++;
                }

                callback.onSuccess(totalNumberOfShopsWorld, shopTypeWorld, shopCountryWorld, totalNumberOfShopsBucuresti, shopTypeBucuresti, shopSectorBucuresti);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                callback.onFailed();
            }
        });
    }

    @Override
    public void updateAdminNameForBucharest() {
        mStoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {

                    while (mPaused) {
                        // An infinite loop that keeps on going until the pause flag is set to false
                    }
                    Shop marker = markerSnapshot.getValue(Shop.class);

                    if (marker.getCity().equals("București")) {
                        getAdminNameForLocation(new OnGetAdminNameCallback() {
                            @Override
                            public void onSuccess(String adminName) {
                                mPaused = false;
                                mStoreRef.child(markerSnapshot.getKey()).child("adminName").setValue(adminName, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                    }
                                });
                            }

                            @Override
                            public void onFailed() {

                            }
                        }, new LatLng(marker.getLat(), marker.getLon()));

                        mPaused = true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void deleteShopWithTypeInCity(final String type, final String city) {

        Query query = mStoreRef.orderByChild("type").equalTo(type);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {

                    Shop marker = markerSnapshot.getValue(Shop.class);

                    if(marker.getCity() != null){
                        if(marker.getCity().equals(city)){
                            mStoreRef.child(markerSnapshot.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d(TAG, "deleted shop");
                                    }else{

                                    }
                                }
                            });

                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getAdminNameForLocation(final OnGetAdminNameCallback callback, final LatLng location) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PlacesService.getAdminName(new OnGetAdminNameCallback() {
                        @Override
                        public void onSuccess(String adminName) {
                            callback.onSuccess(adminName);
                            Log.d(TAG, "adminName is: "+adminName);
                        }

                        @Override
                        public void onFailed() {

                        }
                    }, location.latitude, location.longitude);
                } catch (ApiFailedException e) {
                    //try again
                    getAdminNameForLocation(new OnGetAdminNameCallback() {
                        @Override
                        public void onSuccess(String adminName) {
                            callback.onSuccess(adminName);
                        }

                        @Override
                        public void onFailed() {

                        }
                    }, location);
                }
            }
        });

        thread.start();
    }

    @Override
    public void getShopsAddedToday(final OnGetShopsAddedTodayListener mapPresenter, String userId) {

        final ArrayList<Shop> addedShopsToday = new ArrayList<>();
        //filter data based on logged in user
        Query query = mStoreRef.orderByChild("createdBy").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    Shop store = markerSnapshot.getValue(Shop.class);
                    Date createdAt = new Date(store.getCreatedAt());
                    long now = new Date().getTime();
                    Date nowDate = new Date(now);

                    if (Util.isSameDay(createdAt, nowDate)) {
                        addedShopsToday.add(store);
                    }
                }

                mapPresenter.onGetShopsAddedTodaySuccess(addedShopsToday);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //resolved reports are not taken into account for this check
    @Override
    public void checkIfDuplicateReport(final OnDuplicateReportCallback mapPresenter, final Report currentReportedShop) {
        final ArrayList<Report> reports = new ArrayList<>();

        Query query = mReportRef.orderByChild("reportedBy").equalTo(currentReportedShop.getReportedBy());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    Report report = reportSnapshot.getValue(Report.class);
                    assert report != null;
                    if(!report.isResolved()){
                        reports.add(report);
                    }
                }

                if (reports.contains(currentReportedShop)) {
                    mapPresenter.isDuplicate();
                } else {
                    mapPresenter.isNotDuplicate();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void writeReportToDatabase(final OnReportWrittenToDatabaseCallback mapPresenter, final Report report) {
        mReportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    mReportRef.push().setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mapPresenter.onSuccess();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mapPresenter.onFailed(databaseError.getMessage());
            }
        });
    }

}
