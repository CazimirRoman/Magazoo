package cazimir.com.magazoo.repository;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.map.OnAddListenerForNewMarkerAdded;
import cazimir.com.magazoo.presenter.map.OnAddMarkerToDatabaseListener;
import cazimir.com.magazoo.presenter.map.OnDeleteShopListener;
import cazimir.com.magazoo.presenter.map.OnDuplicateReportListener;
import cazimir.com.magazoo.presenter.map.OnGetMarkersListener;
import cazimir.com.magazoo.presenter.map.OnGetShopsAddedTodayListener;
import cazimir.com.magazoo.ui.map.OnGetReportsFromDatabaseListener;
import cazimir.com.magazoo.ui.map.OnReportWrittenToDatabaseListener;
import cazimir.com.magazoo.utils.Util;

/**
 * TODO: Add a class header comment!
 */
public class Repository implements IRepository {

    private static final String TAG = Repository.class.getSimpleName();

    private DatabaseReference mStoreRef = FirebaseDatabase.getInstance().getReference("Stores");
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
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Shop> markersInVisibleArea = new ArrayList<>();
                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    Shop marker = markerSnapshot.getValue(Shop.class);
                    //update model with id from firebase
                    if (marker != null) {
                        marker.setId(markerSnapshot.getKey());
                        if (bounds.contains(new LatLng(marker.getLat(), marker.getLon()))) {
                            markersInVisibleArea.add(marker);
                        }
                    }
                }

                Log.d(TAG, "markersInVisibleArea: " + markersInVisibleArea.size());
                mapPresenter.onGetAllMarkersSuccess(markersInVisibleArea);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                mapPresenter.onGetAllMarkersFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void addMarkerToDatabase(final OnAddMarkerToDatabaseListener mapPresenter, Shop shop) {

        String shopId = mStoreRef.push().getKey();
        shop.setId(shopId);

        mStoreRef.child(shopId).setValue(shop).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mapPresenter.onAddMarkerSuccess();
                } else {
                    mapPresenter.onAddMarkerFailed(task.getException().getMessage());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mapPresenter.onAddMarkerFailed(e.getMessage());
            }
        });
    }

    @Override
    public void deleteShop(final OnDeleteShopListener mapPresenter, String id) {
        mStoreRef.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "deleteShop: success!");
                    mapPresenter.onDeleteSuccess();
                }else{
                    mapPresenter.onDeleteFailed(task.getException().toString());
                }
            }
        });
    }

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
    public void checkIfDuplicateReport(final OnDuplicateReportListener mapPresenter, final Report currentReportedShop) {
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
                    mapPresenter.isDuplicateReport();
                } else {
                    mapPresenter.isNotDuplicateReport();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void writeReportToDatabase(final OnReportWrittenToDatabaseListener mapPresenter, final Report report) {
        mReportRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                {
                    mReportRef.push().setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mapPresenter.onReportWrittenSuccess();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mapPresenter.onReportWrittenFailed(databaseError.getMessage());
            }
        });
    }

}
