package magazoo.magazine.langa.tine.presenter;

import java.util.ArrayList;

import magazoo.magazine.langa.tine.model.Marker;

/**
 * TODO: Add a class header comment!
 */
public interface OnGetAllMarkersListener {
    void onGetAllMarkersSuccess(ArrayList<Marker> markers);
    void onGetAllMarkersFailed(String message);
}
