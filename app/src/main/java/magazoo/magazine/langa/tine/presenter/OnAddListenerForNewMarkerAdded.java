package magazoo.magazine.langa.tine.presenter;

import java.util.ArrayList;

import magazoo.magazine.langa.tine.model.Marker;

/**
 * TODO: Add a class header comment!
 */
public interface OnAddListenerForNewMarkerAdded {
    void onAddListenerForNewMarkerAddedSuccess(Marker marker, String title);
    void onAddListenerForNewMarkerAddedFailed();
}
