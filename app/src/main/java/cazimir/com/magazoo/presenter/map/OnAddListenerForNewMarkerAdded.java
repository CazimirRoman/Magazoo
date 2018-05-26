package cazimir.com.magazoo.presenter.map;

import cazimir.com.magazoo.model.Shop;

/**
 * TODO: Add a class header comment!
 */
public interface OnAddListenerForNewMarkerAdded {
    void onAddListenerForNewMarkerAddedSuccess(Shop marker, String title);
    void onAddListenerForNewMarkerAddedFailed();
}
