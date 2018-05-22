package cazimir.com.magazoo.presenter;

import com.google.android.gms.maps.model.LatLngBounds;

import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.ui.map.OnIsAllowedToReportListener;

/**
 * TODO: Add a class header comment!
 */
public interface IMapPresenter {
    void checkIfDuplicateReport(Report currentReportedShop);
    boolean isUserLoggedIn();
    String getUserEmail();
    void addListenerForNewMarkerAdded();
    void getAllMarkers(LatLngBounds mBounds);
    void addMarkerToFirebase(Shop markerToAdd);
    void deleteShopFromDB(String id);
}
