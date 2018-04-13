package magazoo.magazine.langa.tine.repository;

import com.google.android.gms.maps.model.LatLngBounds;

import magazoo.magazine.langa.tine.model.Shop;
import magazoo.magazine.langa.tine.presenter.OnAddListenerForNewMarkerAdded;
import magazoo.magazine.langa.tine.presenter.OnAddMarkerToDatabaseListener;
import magazoo.magazine.langa.tine.presenter.OnGetAllMarkersListener;
import magazoo.magazine.langa.tine.presenter.OnGetShopsAddedTodayListener;
import magazoo.magazine.langa.tine.ui.map.OnGetReportsFromDatabaseListener;

public interface IRepository {
    void getReportsAddedToday(OnGetReportsFromDatabaseListener listener, String userId);
    void getShopsAddedToday(OnGetShopsAddedTodayListener listener, String userId);
    void addChildEventListenerForMarker(OnAddListenerForNewMarkerAdded listener);
    void getAllMarkers(OnGetAllMarkersListener listener, LatLngBounds bounds);
    void addMarkerToDatabase(OnAddMarkerToDatabaseListener listener, Shop markerToAdd);
}
