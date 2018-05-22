package cazimir.com.magazoo.repository;

import com.google.android.gms.maps.model.LatLngBounds;

import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.presenter.OnAddListenerForNewMarkerAdded;
import cazimir.com.magazoo.presenter.OnAddMarkerToDatabaseListener;
import cazimir.com.magazoo.presenter.OnDeleteShopListener;
import cazimir.com.magazoo.presenter.OnGetAllMarkersListener;
import cazimir.com.magazoo.presenter.OnGetShopsAddedTodayListener;
import cazimir.com.magazoo.ui.map.OnGetReportsFromDatabaseListener;

public interface IRepository {
    void getReportsAddedToday(OnGetReportsFromDatabaseListener listener, String userId);
    void getShopsAddedToday(OnGetShopsAddedTodayListener listener, String userId);
    void addChildEventListenerForMarker(OnAddListenerForNewMarkerAdded listener);
    void getAllMarkers(OnGetAllMarkersListener listener, LatLngBounds bounds);
    void addMarkerToDatabase(OnAddMarkerToDatabaseListener listener, Shop markerToAdd);
    void deleteShop(OnDeleteShopListener listener, String id);
}
