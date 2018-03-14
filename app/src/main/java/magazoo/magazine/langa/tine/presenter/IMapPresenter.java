package magazoo.magazine.langa.tine.presenter;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;

import magazoo.magazine.langa.tine.model.Marker;
import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.presenter.authentication.AuthPresenter;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToReportListener;

/**
 * TODO: Add a class header comment!
 */
public interface IMapPresenter {
    void checkIfAllowedToReport(OnIsAllowedToReportListener listener);
    void checkIfDuplicateReport(Report currentReportedShop);
    boolean isUserLoggedIn();
    String getUserEmail();
    void addListenerForNewMarkerAdded();
    void getAllMarkers(LatLngBounds mBounds);
    void addMarkerToFirebase(Marker markerToAdd);
}
