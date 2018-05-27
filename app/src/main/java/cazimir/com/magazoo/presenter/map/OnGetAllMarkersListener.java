package cazimir.com.magazoo.presenter.map;

import java.util.ArrayList;

import cazimir.com.magazoo.model.Shop;

/**
 * TODO: Add a class header comment!
 */
public interface OnGetAllMarkersListener {
    void onGetAllMarkersSuccess(ArrayList<Shop> markers);
    void onGetAllMarkersFailed(String message);
}
