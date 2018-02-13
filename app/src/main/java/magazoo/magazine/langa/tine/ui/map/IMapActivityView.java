package magazoo.magazine.langa.tine.ui.map;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import magazoo.magazine.langa.tine.model.Marker;

/**
 * TODO: Add a class header comment!
 */
public interface IMapActivityView {
    void showToast(String message);
    void showAddShopDialog();
    void showErrorDialog(String title, String message, int errorType);
    void showReportDialog();
    void showReportThanksPopup();
    void showAddThanksPopup();
    void showDuplicateReportErrorDialog(String regards);
    Marker getCurrentOpenShop();
    void showShopLimitErrorDialog();
    void closeShopDetails();
    void closeReportDialog();
    void addNewlyAddedMarkerToMap(Marker marker, String title);
    void addMarkersToMap(ArrayList<Marker> markers);
    void closeAddShopDialog();
}
