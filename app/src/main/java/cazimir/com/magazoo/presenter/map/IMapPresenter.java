package cazimir.com.magazoo.presenter.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.ui.map.OnIsAllowedToAddListener;
import cazimir.com.magazoo.ui.map.OnIsAllowedToReportListener;

/**
 * TODO: Add a class header comment!
 */
public interface IMapPresenter {
    void writeReportToDatabase(Report currentReportedShop);
    void checkIfAllowedToAdd(final OnIsAllowedToAddListener mapActivityView);
    boolean isUserLoggedIn();
    String getUserEmail();
    void getAllMarkers(LatLngBounds mBounds);
    void addMarkerToFirebase(Shop markerToAdd);
    void deleteShopFromDB(String id);
    boolean isAdmin();
}
