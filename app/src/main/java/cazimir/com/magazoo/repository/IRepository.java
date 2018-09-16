package cazimir.com.magazoo.repository;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.map.OnAddListenerForNewMarkerAdded;
import cazimir.com.magazoo.presenter.map.OnAddMarkerToDatabaseListener;
import cazimir.com.magazoo.presenter.map.OnDeleteShopListener;
import cazimir.com.magazoo.presenter.map.OnDuplicateReportCallback;
import cazimir.com.magazoo.presenter.map.OnGetMarkersListener;
import cazimir.com.magazoo.presenter.map.OnGetShopsAddedTodayListener;
import cazimir.com.magazoo.reports.OnGetAllShopsReportCallback;
import cazimir.com.magazoo.ui.map.OnGetReportsFromDatabaseListener;
import cazimir.com.magazoo.ui.map.OnReportWrittenToDatabaseCallback;

public interface IRepository {
    void getReportsAddedToday(OnGetReportsFromDatabaseListener listener, String userId);

    void getAdminNameForLocation(OnGetAdminNameCallback callback, LatLng location);

    void getShopsAddedToday(OnGetShopsAddedTodayListener listener, String userId);
    void getMarkers(OnGetMarkersListener listener, LatLngBounds bounds);
    void addMarkerToDatabase(OnAddMarkerToDatabaseListener listener, Shop markerToAdd);
    void deleteShop(OnDeleteShopListener listener, String id);

    void getAllShopsForReport(OnGetAllShopsReportCallback callback);
    void updateAdminNameForBucharest();

    void deleteShopWithTypeInCity(String type, String city);
    void checkIfDuplicateReport(final OnDuplicateReportCallback mapPresenter, final Report currentReportedShop);
    void writeReportToDatabase(final OnReportWrittenToDatabaseCallback mapPresenter, final Report report);
}
