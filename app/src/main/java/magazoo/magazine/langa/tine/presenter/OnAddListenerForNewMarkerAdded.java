package magazoo.magazine.langa.tine.presenter;

import magazoo.magazine.langa.tine.model.Shop;

/**
 * TODO: Add a class header comment!
 */
public interface OnAddListenerForNewMarkerAdded {
    void onAddListenerForNewMarkerAddedSuccess(Shop marker, String title);
    void onAddListenerForNewMarkerAddedFailed();
}
