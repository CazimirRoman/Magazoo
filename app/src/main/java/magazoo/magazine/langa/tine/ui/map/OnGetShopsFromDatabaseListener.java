package magazoo.magazine.langa.tine.ui.map;


import java.util.ArrayList;

import magazoo.magazine.langa.tine.model.Shop;

public interface OnGetShopsFromDatabaseListener {
    void onDataFetched(ArrayList<Shop> shopsAddedToday);
}
