package magazoo.magazine.langa.tine.presenter;

import com.google.android.gms.maps.model.LatLngBounds;

import magazoo.magazine.langa.tine.model.Shop;
import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToReportListener;

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
}
