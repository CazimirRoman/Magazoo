package cazimir.com.magazoo.presenter;

import java.util.ArrayList;

import cazimir.com.magazoo.model.Shop;

/**
 * TODO: Add a class header comment!
 */
public interface OnGetShopsAddedTodayListener {
    void onGetShopsAddedTodaySuccess(ArrayList<Shop> shopsAddedToday);
    void onGetShopsAddedTodayFailed();
}
