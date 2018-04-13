package magazoo.magazine.langa.tine.presenter;

import java.util.ArrayList;

import magazoo.magazine.langa.tine.model.Shop;

/**
 * TODO: Add a class header comment!
 */
public interface OnGetShopsAddedTodayListener {
    void onGetShopsAddedTodaySuccess(ArrayList<Shop> shopsAddedToday);
    void onGetShopsAddedTodayFailed();
}
