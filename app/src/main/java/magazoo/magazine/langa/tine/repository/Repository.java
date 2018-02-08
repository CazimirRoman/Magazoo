package magazoo.magazine.langa.tine.repository;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.ui.map.OnGetReportsFromDatabaseListener;
import magazoo.magazine.langa.tine.utils.Util;

/**
 * TODO: Add a class header comment!
 */
public class Repository implements IRepository {

    private DatabaseReference mStoreRef = FirebaseDatabase.getInstance().getReference("Stores");
    private DatabaseReference mReportRef = FirebaseDatabase.getInstance().getReference("Reports");

    public void getReportsAddedToday(final OnGetReportsFromDatabaseListener listener) {

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

}
