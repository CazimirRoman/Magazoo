package magazoo.magazine.langa.tine.presenter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import magazoo.magazine.langa.tine.R;
import magazoo.magazine.langa.tine.constants.Constants;
import magazoo.magazine.langa.tine.model.Marker;
import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.repository.Repository;
import magazoo.magazine.langa.tine.ui.map.OnGetReportsFromDatabaseListener;
import magazoo.magazine.langa.tine.ui.map.OnGetShopsFromDatabaseListener;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToAddListener;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToReportListener;
import magazoo.magazine.langa.tine.utils.Util;

/**
 * TODO: Add a class header comment!
 */
public class MapPresenter implements IMapPresenter {

    private Repository mRepository = new Repository();

    public void checkIfAllowedToReport(final OnIsAllowedToReportListener listener) {

        mRepository.getReportsAddedToday(new OnGetReportsFromDatabaseListener() {
            @Override
            public void onDataFetched(ArrayList<Report> reportsAddedToday) {
                if (isUnderTheReportLimit(reportsAddedToday)) {
                    listener.isAllowedToReport();
                } else {
                    listener.isNotAllowedToReport();
                }
            }
        });

    }

    private boolean isUnderTheReportLimit(ArrayList<Report> reportsAddedToday) {
        return reportsAddedToday.size() <= Constants.REPORT_SHOP_LIMIT;
    }

    public void checkIfAllowedToAdd(final OnIsAllowedToAddListener listener) {
        getShopsAddedToday(new OnGetShopsFromDatabaseListener() {
            @Override
            public void onDataFetched(ArrayList<Marker> shopsAddedToday) {
                if (isUnderTheAddLimit(shopsAddedToday)) {
                    listener.isAllowedToAdd();
                } else {
                    listener.isNotAllowedToAdd();
                }
            }
        });
    }

    private boolean isUnderTheAddLimit(ArrayList<Marker> shopsAddedToday) {
        return shopsAddedToday.size() <= Constants.ADD_SHOP_LIMIT;
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
}
