package magazoo.magazine.langa.tine.ui.map;

import com.google.android.gms.maps.model.LatLng;

/**
 * TODO: Add a class header comment!
 */
public interface IMapActivityView {
    void showAddShopDialog();
    void showErrorDialog(String title, String message, int errorType);
    void showReportPopup();
    void showDuplicateLocationReportErrorDialog();
}
