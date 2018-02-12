package magazoo.magazine.langa.tine.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import magazoo.magazine.langa.tine.model.Marker;
import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.presenter.OnDuplicateLocationReportListener;
import magazoo.magazine.langa.tine.presenter.authentication.AuthenticationPresenter;
import magazoo.magazine.langa.tine.ui.map.OnGetReportsFromDatabaseListener;
import magazoo.magazine.langa.tine.ui.map.OnGetShopsFromDatabaseListener;
import magazoo.magazine.langa.tine.ui.map.OnReportWrittenToDatabaseListener;
import magazoo.magazine.langa.tine.utils.Util;

/**
 * TODO: Add a class header comment!
 */
public class Repository implements IRepository {

    private DatabaseReference mStoreRef = FirebaseDatabase.getInstance().getReference("Stores");
    private DatabaseReference mReportRef = FirebaseDatabase.getInstance().getReference("Reports");

    public void getReportsAddedToday(final OnGetReportsFromDatabaseListener listener, String userId) {

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

                listener.onDataFetched(reportsToday);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    public void checkIfDuplicateLocationReport(final OnDuplicateLocationReportListener listener, String userId, final Report currentReportedShop) {
        final ArrayList<Report> locationReports = new ArrayList<>();

        Query query = mReportRef.orderByChild("reportedBy").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    Report report = markerSnapshot.getValue(Report.class);
                    if (report.getRegards().equals("location")) {
                        locationReports.add(report);
                    }
                }

                if (!locationReports.contains(currentReportedShop)) {

                    listener.isNotDuplicateLocationReport();
                } else {
                    listener.isDuplicateLocationReport();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

}
