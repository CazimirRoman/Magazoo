package cazimir.com.magazoo.ui.map;

import java.util.ArrayList;

import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.model.Shop;

/**
 * TODO: Add a class header comment!
 */
public interface IMapActivityView {
    void showToast(String message);
    void showErrorDialog(String title, String message, int errorType);
    void showReportDialog();
    void showReportThanksPopup();
    void showAddThanksPopup();
    Report getCurrentReportedShop();
    void closeShopDetails();
    void openShopDetails();
    void closeReportDialog();
    void addNewlyAddedMarkerToMap(Shop marker, String title);
    void addMarkersToMap(ArrayList<Shop> markers);
    void closeAddShopDialog();
}
