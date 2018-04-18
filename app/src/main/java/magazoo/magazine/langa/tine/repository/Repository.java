package magazoo.magazine.langa.tine.repository;

import android.support.annotation.NonNull;

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

import magazoo.magazine.langa.tine.model.Shop;
import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.presenter.OnAddListenerForNewMarkerAdded;
import magazoo.magazine.langa.tine.presenter.OnAddMarkerToDatabaseListener;
import magazoo.magazine.langa.tine.presenter.OnDuplicateReportListener;
import magazoo.magazine.langa.tine.presenter.OnGetAllMarkersListener;
import magazoo.magazine.langa.tine.presenter.OnGetShopsAddedTodayListener;
import magazoo.magazine.langa.tine.ui.map.OnGetReportsFromDatabaseListener;
import magazoo.magazine.langa.tine.ui.map.OnReportWrittenToDatabaseListener;
import magazoo.magazine.langa.tine.utils.Util;

/**
 * TODO: Add a class header comment!
 */
public class Repository implements IRepository {

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
    public void addChildEventListenerForMarker(final OnAddListenerForNewMarkerAdded mapPresenter) {
        mStoreRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Shop marker = dataSnapshot.getValue(Shop.class);
                assert marker != null;
                mapPresenter.onAddListenerForNewMarkerAddedSuccess(marker, marker.getId());
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

    @Override
    public void getAllMarkers(final OnGetAllMarkersListener mapPresenter, final LatLngBounds bounds) {
        mStoreRef.addValueEventListener(new ValueEventListener() {
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

    public void checkIfDuplicateReport(final OnDuplicateReportListener mapPresenter, String userId, final Report currentReportedShop) {
        final ArrayList<Report> reports = new ArrayList<>();

        Query query = mReportRef.orderByChild("reportedBy").equalTo(currentReportedShop.getReportedBy());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()) {
                    Report report = reportSnapshot.getValue(Report.class);
                    reports.add(report);
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
