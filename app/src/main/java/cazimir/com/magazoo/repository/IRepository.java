package cazimir.com.magazoo.repository;

import com.google.android.gms.maps.model.LatLngBounds;

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

public interface IRepository {
    void checkIfDuplicateReport(final OnDuplicateReportListener mapPresenter, final Report currentReportedShop);
    void writeReportToDatabase(final OnReportWrittenToDatabaseListener mapPresenter, final Report report);
    void getReportsAddedToday(OnGetReportsFromDatabaseListener listener, String userId);
    void getShopsAddedToday(OnGetShopsAddedTodayListener listener, String userId);
    void getMarkers(OnGetMarkersListener listener, LatLngBounds bounds);
    void addMarkerToDatabase(OnAddMarkerToDatabaseListener listener, Shop markerToAdd);
    void deleteShop(OnDeleteShopListener listener, String id);
}
