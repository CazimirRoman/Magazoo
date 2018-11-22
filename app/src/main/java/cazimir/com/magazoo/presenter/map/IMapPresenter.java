package cazimir.com.magazoo.presenter.map;

import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import cazimir.com.magazoo.model.Shop;
import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.ui.map.OnIsAllowedToAddCallback;

/**
 * TODO: Add a class header comment!
 */
public interface IMapPresenter {
    void writeReportToDatabase(Report currentReportedShop);
    void checkIfAllowedToAddShop();
    String getUserEmail();
    void getAllMarkers();
    void addMarkerToFirebase(Shop markerToAdd);
    void deleteShopFromDB(Shop shop);
    boolean isAdmin();
}
