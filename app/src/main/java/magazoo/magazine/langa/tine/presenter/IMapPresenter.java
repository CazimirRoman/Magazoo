package magazoo.magazine.langa.tine.presenter;

import magazoo.magazine.langa.tine.model.Report;
import magazoo.magazine.langa.tine.ui.map.OnIsAllowedToReportListener;

/**
 * TODO: Add a class header comment!
 */
public interface IMapPresenter {
    void checkIfAllowedToReport(OnIsAllowedToReportListener listener);
    void checkIfDuplicateLocationReport(Report currentReportedShop);
    boolean isUserLoggedIn();
    String getUserEmail();
}
