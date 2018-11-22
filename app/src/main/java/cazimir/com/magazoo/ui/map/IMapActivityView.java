package cazimir.com.magazoo.ui.map;

import java.util.ArrayList;

import cazimir.com.magazoo.model.Report;
import cazimir.com.magazoo.model.Shop;

/**
 * TODO: Add a class header comment!
 */
public interface IMapActivityView {
    void showToast(String message);
    void showReportDialog();
    void showReportThanksPopup();
    void showAddThanksPopup();
    Report getCurrentReportedShop();
    void closeShopDetails();
    void openShopDetails();
    void closeReportDialog();
    void addShopsToLocalStorage(ArrayList<Shop> shops);
    void closeAddShopDialog();
    void getAllMarkers();
    void showProgressBar();
    void hideProgressBar();
    void isAllowedToAdd();
    void isNotAllowedToAdd();
    void addMarkerToLocalDatabase(Shop shop);
    void removeMarkerFromLocalDatabase(Shop shop);
}
